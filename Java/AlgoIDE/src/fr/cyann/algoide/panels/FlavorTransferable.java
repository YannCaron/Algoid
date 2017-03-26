/* 
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.algoide.panels;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * The FlavorTransferable class.
 * Creation date: 29 sept. 2013.
 * @author CyaNn 
 * @version v0.1
 */
public class FlavorTransferable implements Transferable {

	private Object data = null;
	private DataFlavor flavor;

	public FlavorTransferable(Object o, DataFlavor df) {
		data = o;
		flavor = df;
	}

	public Object getTransferData(DataFlavor df) throws
		UnsupportedFlavorException, IOException {
		if (!flavor.isMimeTypeEqual(flavor)) {
			throw new UnsupportedFlavorException(df);
		}
		return data;
	}

	public boolean isDataFlavorSupported(DataFlavor df) {
		return flavor.isMimeTypeEqual(df);
	}

	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] ret = {flavor};
		return ret;
	}
}
