/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.editing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import org.prop4j.Literal;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.job.LongRunningMethod;
import de.ovgu.featureide.fm.core.job.monitor.IMonitor;

/**
 * Returns a copy of the given model with obfuscated feature names and descriptions.
 *
 * @author Sebastian Krieter
 */
public class FeatureModelObfuscator implements LongRunningMethod<IFeatureModel> {

	private final static int lengthFactor = 8;
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	private final IFeatureModel orgFeatureModel;
	private final IFeatureModelFactory factory;
	private final byte[] salt;

	private MessageDigest digest;
	private IFeatureModel obfuscatedFeatureModel;

	/**
	 * Instantiates the obfuscater with a default salt value (Not recommended!). Alternatively, {@link #FeatureModelObfuscator(IFeatureModel, String)} can be
	 * used to set a salt value.
	 *
	 * @param featureModel the feature model to be obfuscated
	 */
	public FeatureModelObfuscator(IFeatureModel featureModel) {
		this(featureModel, "");
	}

	/**
	 * Instantiates the obfuscater using the given feature model and salt value.
	 *
	 * @param featureModel the feature model to be obfuscated
	 * @param salt a fix salt value
	 */
	public FeatureModelObfuscator(IFeatureModel featureModel, String salt) {
		this.salt = salt.getBytes(StandardCharsets.UTF_8);
		orgFeatureModel = featureModel;
		IFeatureModelFactory factoryById;
		try {
			factoryById = FMFactoryManager.getFactoryById(orgFeatureModel.getFactoryID());
		} catch (final NoSuchExtensionException e) {
			factoryById = FMFactoryManager.getDefaultFactory();
		}
		factory = factoryById;
	}

	@Override
	public IFeatureModel execute(IMonitor monitor) throws Exception {
		digest = MessageDigest.getInstance("SHA-256");
		obfuscatedFeatureModel = factory.createFeatureModel();
		obfuscateStructure(orgFeatureModel.getStructure().getRoot(), null);
		obfuscateConstraints();
		return obfuscatedFeatureModel;
	}

	private void obfuscateStructure(IFeatureStructure orgFeatureStructure, IFeature parentFeature) {
		final IFeature orgFeature = orgFeatureStructure.getFeature();

		final IFeature obfuscatedFeature = factory.createFeature(obfuscatedFeatureModel, getObfuscatedFeatureName(orgFeature.getName()));
		final String description = orgFeatureStructure.getFeature().getProperty().getDescription();
		if ((description != null) && !description.isEmpty()) {
			obfuscatedFeature.getProperty().setDescription(getObfuscatedDescription(description));
		}

		obfuscatedFeatureModel.addFeature(obfuscatedFeature);
		if (parentFeature == null) {
			obfuscatedFeatureModel.getStructure().setRoot(obfuscatedFeature.getStructure());
		} else {
			parentFeature.getStructure().addChild(obfuscatedFeature.getStructure());
		}

		for (final IFeatureStructure orgChildStructure : orgFeatureStructure.getChildren()) {
			obfuscateStructure(orgChildStructure, obfuscatedFeature);
		}
	}

	private void obfuscateConstraints() {
		for (final IConstraint constraint : orgFeatureModel.getConstraints()) {
			final Node clone = constraint.getNode().clone();
			for (final Literal literal : clone.getLiterals()) {
				literal.var = getObfuscatedFeatureName(literal.var.toString());
			}
			obfuscatedFeatureModel.addConstraint(factory.createConstraint(obfuscatedFeatureModel, clone));
		}
	}

	private String getObfuscatedFeatureName(String name) {
		final char[] result = new char[(4 * lengthFactor) + 2];
		result[0] = 'F';

		return obfuscate(name, result);
	}

	private String getObfuscatedDescription(String description) {
		final char[] result = new char[(4 * lengthFactor) + 2];
		result[0] = 'D';

		return obfuscate(description, result);
	}

	private String obfuscate(String string, final char[] result) {
		result[1] = '_';
		final int index = 2;

		digest.reset();
		digest.update(salt);
		digest.update(string.getBytes(StandardCharsets.UTF_8));
		final byte[] hash = digest.digest();

		return encodeBase64(result, index, hash);
	}

	private static String encodeBase64(final char[] result, int index, byte[] hash) {
		final int length = ((result.length - index) >> 2) * 3;
		if (hash.length < length) {
			hash = Arrays.copyOf(hash, length);
		}
		for (int i = 0; i < length; i += 3) {
			int x = 0xff & hash[i];
			x |= (0xff & hash[i + 1]) << 8;
			x |= (0xff & hash[i + 2]) << 16;
			for (int j = 0; j < 4; j++) {
				result[index++] = alphabet.charAt(x & 0x3f);
				x >>>= 6;
			}
		}
		return new String(result);
	}

	public static String getRandomSalt() {
		final byte[] saltArray = new byte[24];
		new SecureRandom().nextBytes(saltArray);
		return encodeBase64(new char[32], 0, saltArray);
	}

}
