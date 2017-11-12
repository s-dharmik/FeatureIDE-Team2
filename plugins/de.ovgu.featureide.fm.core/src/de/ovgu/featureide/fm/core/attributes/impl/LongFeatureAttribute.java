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
package de.ovgu.featureide.fm.core.attributes.impl;

/**
 * TODO description
 *
 * @author Joshua Sprey
 * @author Chico Sundermann
 */
public class LongFeatureAttribute extends FeatureAttribute {

	private final Long value;
	protected final String attributeType = "long";

	/**
	 * @param name
	 * @param unit
	 * @param value
	 * @param recursive
	 * @param configureable
	 */

	public LongFeatureAttribute(String name, String unit, Long value, boolean recursive, boolean configureable) {
		super(name, unit, recursive, configureable);
		this.value = value;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		// this.value = value;
	}

	@Override
	public String getType() {
		return attributeType;
	}
}
