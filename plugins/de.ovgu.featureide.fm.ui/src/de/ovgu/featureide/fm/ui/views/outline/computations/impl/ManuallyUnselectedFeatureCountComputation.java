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
package de.ovgu.featureide.fm.ui.views.outline.computations.impl;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation;

/**
 * TODO description
 *
 * @author User
 */
public class ManuallyUnselectedFeatureCountComputation implements IConfigurationComputation {

	private final Configuration config;
	private static final String HEADER_STRING = "Number of manually unselected features";

	public ManuallyUnselectedFeatureCountComputation(Configuration config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation#getResult()
	 */
	@Override
	public Object[] getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation#getResultString()
	 */
	@Override
	public String getResultString() {
		return Integer.toString(calculateCount());
	}

	/*
	 * (non-Javadoc)
	 * @see de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation#getConfiguration()
	 */
	@Override
	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return config;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation#getHeaderString()
	 */
	@Override
	public String getHeaderString() {
		return HEADER_STRING;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ovgu.featureide.fm.ui.views.outline.computations.IConfigurationComputation#supportsType(java.lang.Object)
	 */
	@Override
	public boolean supportsType(Object element) {
		return true;
	}

	private int calculateCount() {
		int count = 0;
		for (final SelectableFeature sf : config.getFeatures()) {
			if ((sf.getManual() == Selection.UNSELECTED) && (sf.getAutomatic() == Selection.UNDEFINED)) {
				count++;
			}

		}
		return count;
	}

}
