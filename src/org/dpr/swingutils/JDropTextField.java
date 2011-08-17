package org.dpr.swingutils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.JTextField;

public class JDropTextField extends JTextField implements DropTargetListener {

	public JDropTextField(String string, int i) {
		super(string, i);
	}

	public JDropTextField(int i) {
		super(i);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		//

	}

	public void dragExit(DropTargetEvent dte) {
		//

	}

	public void dragOver(DropTargetDragEvent dtde) {
		//

	}

	public void drop(DropTargetDropEvent dtde) {
		// System.out.println("drop");
		// System.out.println(dtde.getDropAction());
		// System.out.println();
		// Check the drop action
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try {
				boolean result = false;

				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					result = dropFile(transferable);
				} else {
					result = false;
				}
				dtde.dropComplete(result);

			} catch (Exception e) {
				System.out.println("Exception while handling drop " + e);
				dtde.rejectDrop();
			}
		} else {
			System.out.println("Drop target rejected drop");
			dtde.dropComplete(false);
		}

	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// System.out.println("dropActionChanged");

	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable) throws IOException,
			UnsupportedFlavorException, MalformedURLException {
		List fileList = (List) transferable
				.getTransferData(DataFlavor.javaFileListFlavor);
		File transferFile = (File) fileList.get(0);

		final String transferURL = transferFile.getAbsolutePath();
		// System.out.println("File URL is " + transferURL);
		this.setText(transferURL);

		return true;
	}
}