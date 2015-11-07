package ui;

import java.io.IOException;

import org.junit.Test;


import org.fxmisc.richtext.InlineCssTextArea;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/*
public class UITest extends GuiTest {

	@Override
	protected Parent getRootNode() {
		Parent parent = null;
		try {
			parent = FXMLLoader.load(getClass().getResource("view/CelebiView.fxml"));
			return parent;
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
		return parent;
	}

	@Test
	public void setTextAreaText() {
		InlineCssTextArea commandArea = (InlineCssTextArea) find("#command-area");
		click("#command-area").type("add");
		assert(commandArea.getText().equals("add"));
	}

}
*/