package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.intellij.uiDesigner.core.Util;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;

public class helloworld extends AnAction {

    private String xmlfilename;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
       // JOptionPane.showInputDialog("请输入");

        //1 获取layout的名字
        //获取project对象
        Project project = e.getProject();
        //获取编辑区对象
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if(null == editor)
        {
            return;
        }
        SelectionModel model = editor.getSelectionModel();
        //如果用户没有选择正确 就弹出一个对话框
        xmlfilename = model.getSelectedText();
        if(TextUtils.isEmpty(xmlfilename))
        {
            //获取光标所在位置的布局文件
            xmlfilename = getCurrentLayout(editor);
            if(TextUtils.isEmpty(xmlfilename))
            {
                xmlfilename = Messages.showInputDialog(project,"请输入layout名","未输入",Messages.getInformationIcon());
                if(TextUtils.isEmpty(xmlfilename))
                {
                    showPopupBallon("用户没有输入layout",editor,5);
                }
            }
        }

        //运行到这里就已经得到了layout的名字了
        //找到对应的xml文件 把xml中所有的ID获取到
    }


    private void showPopupBallon(final String result,final Editor editor,final int time){
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result,null,new JBColor(new Color(116,214,238),new Color(76,112,117)),null)
                        .setFadeoutTime(time * 1000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
    }

    private String getCurrentLayout(Editor editor)
    {
        Document document = editor.getDocument();
        //取到插字光标模式对象
        CaretModel caretModel = editor.getCaretModel();
        //得到光标的位置
        int offset = caretModel.getOffset();
        //得到一行开始和结束的地方
        int lineStartOffset = document.getLineStartOffset(offset);
        int lineEndOffset = document.getLineEndOffset(offset);
        int lineNumber = document.getLineNumber(offset);

        //获取一行内容
        String lineContent = document.getText(new TextRange(lineStartOffset,lineEndOffset));
        String layoutMatching = "R.layout.";
        if(!TextUtils.isEmpty(lineContent) && lineContent.contains(layoutMatching))
        {
            int startPosition = lineContent.indexOf(layoutMatching) + layoutMatching.length();
            int endPosition = lineContent.indexOf(")",startPosition);
            String layoutStr = lineContent.substring(startPosition,endPosition);
            return layoutStr;
        }
        return null;
    }
}
