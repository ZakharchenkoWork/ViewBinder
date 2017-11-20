package main;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class TextBoxes extends AnAction {
    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TextBoxes() {
        // Set the menu item name.
        super("ViewBinder");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = (Editor) DataManager.getInstance().getDataContext().getData(DataConstants.EDITOR);
        int offset = editor.getCaretModel().getOffset();
        editor.getDocument().getCharsSequence();
        String code = "" + editor.getDocument().getCharsSequence();
        if (offset < code.length()) {
            int startBraceIndex = offset;
            int endBraceIndex = offset;
            while (code.charAt(startBraceIndex) != '<') {
                startBraceIndex--;
            }
            while (code.charAt(endBraceIndex) != '>') {
                endBraceIndex++;
            }
            String viewCode = "";

            for (int i = startBraceIndex; i < endBraceIndex; i++) {
                viewCode += code.charAt(i);
            }
            String[] codeParts = viewCode.split(" ");
            String viewClassName = codeParts[0].replace("<", "");
            String viewIdName = "";
            for (int i = 0; i < codeParts.length; i++) {
                if (codeParts[i].contains("android:id")) {
                    String idCodePart = codeParts[i].split("\"")[1];
                    viewIdName = idCodePart.replace("@+id/", "");
                    break;
                }
            }

            if (viewIdName.length() == 0) {
                //Messages.showWarningDialog("No id", "Oops");
                HintManager.getInstance().showInformationHint(editor, "Please set ID for this view");
            } else {
                viewClassName = getClass(viewClassName);
                String result = viewClassName + " " + viewIdName + " = (" + viewClassName+ ") findViewById(R.id." + viewIdName + ");";
                StringSelection stringSelection = new StringSelection(result);
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                HintManager.getInstance().showInformationHint(editor, "Added to clipboard");
            }
        }
    }
    private String getClass(String viewClassName){

        String[] parts = viewClassName.trim().replace(".","---").split("---");
        if(parts.length != 0) {
            return parts[parts.length - 1];
        } else {
            return viewClassName;
        }

    }
}