/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Code;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;

/**
 *
 * @author Aco
 */
public class InstructionBox
{
    private JTextField textBox;
    private JButton commandButton;
    private JButton rootDefault;
    private String instruction;

    public InstructionBox(JTextField box, JButton command)
    {
        this.textBox = box;
        this.commandButton = command;

        start();
    }

    public void showInstruction()
    {
        textBox.setText(instruction);
    }

    public void start()
    {
        instruction = textBox.getText();

        textBox.addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {                
                JRootPane cmdRoot = commandButton.getRootPane();
                rootDefault = cmdRoot.getDefaultButton();
                commandButton.getRootPane().setDefaultButton(commandButton);

                textBox.selectAll();
            }

            public void focusLost(FocusEvent e)
            {
                if(textBox.getText().isEmpty())
                    textBox.setText(instruction);

                commandButton.getRootPane().setDefaultButton(rootDefault);
            }
        });
    }
}
