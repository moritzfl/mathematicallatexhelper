/*         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU General Public License as published by
 *         the Free Software Foundation, either version 3 of the License, or (at
 *         your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful, but
 *         WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *         General Public License for more details.
 *
 *         You should have received a copy of the GNU General Public License
 *         along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.moritzf.latexhelper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * The Class Gui.
 *
 * @author Moritz Floeter
 */
public class Gui extends JFrame implements ActionListener, DocumentListener {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -3765767133973545257L;

    /**
     * The latex source.
     */
    private JTextArea latexSource = new JTextArea();

    private UndoManager undoManager;

    /**
     * The btn save.
     */
    private JButton btnSave = new JButton("Save to desktop");

    /**
     * The btn copy.
     */
    private JButton btnCopy = new JButton("Copy image");

    /**
     * The drawing area.
     */
    private JLabel drawingArea = new JLabel("");

    /**
     * Instantiates a new gui.
     */
    public Gui() {
        super("Mathematical LaTeX Helper");
        Container content = this.getContentPane();
        content.setLayout(new GridLayout(2, 1));
        this.latexSource = new JTextArea();


        initUndo();

        JPanel editorArea = new JPanel();
        editorArea.setLayout(new BorderLayout());
        editorArea.add(new JScrollPane(this.latexSource), BorderLayout.CENTER);

        JPanel btnPnl = new JPanel(new GridLayout(1, 0));
        btnPnl.add(btnCopy);
        btnPnl.add(btnSave);
        editorArea.add(btnPnl, BorderLayout.SOUTH);

        content.add(editorArea);
        content.add(new JScrollPane(this.drawingArea));

        // adding Listeners
        latexSource.getDocument().addDocumentListener(this);
        this.btnCopy.addActionListener(this);
        this.btnSave.addActionListener(this);

        this.latexSource.setText("x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}");

        this.setSize(500, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        WindowFunctions.centerWindow(this);
        WindowFunctions.enableOSXFullscreen(this);

        this.setVisible(true);
    }

    private void initUndo(){
        undoManager = new UndoManager();
        Document doc = latexSource.getDocument();
        doc.addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
        });

        InputMap im = latexSource.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = latexSource.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

        am.put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });
        am.put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        });
    }

    /**
     * Renders the entered expression in the drawing area.
     *
     * @param expression the expression
     */
    public void render(String expression) {
        try {
            if (expression.isEmpty()) {
                expression = "Enter \\quad LaTeX-Expression \\quad to  \\quad begin.";
            }
            TeXFormula formula = new TeXFormula(expression);
            TeXIcon ticon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 22, TeXConstants.UNIT_PIXEL, 80,
                    TeXConstants.ALIGN_LEFT);
            this.drawingArea.setIcon(ticon);

        } catch (Exception ex) {
            try {
                TeXFormula formula = new TeXFormula(
                        "\\text{Rendering failed: check LaTeX expression}");
                TeXIcon ticon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 22, TeXConstants.UNIT_PIXEL, 80,
                        TeXConstants.ALIGN_LEFT);
                this.drawingArea.setIcon(ticon);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Rendering failed in an insane manner", "Gigantic Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        this.validate();
        this.repaint();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.btnSave)) {
            try {
                Export.save(this.latexSource.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this,
                        "<html>Make sure you entered a valid LaTeX-expression.<br>"
                                + " Furthermore ensure that you have writing access to"
                                + " the directory: " + Export.USER_DESKTOP + "<html>",
                        "Could not save", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(this.btnCopy)) {
            try {
                Export.setClipboard(this.latexSource.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Make sure you entered a valid LaTeX-expression", "Could not copy",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        render(this.latexSource.getText());

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        render(this.latexSource.getText());

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        render(this.latexSource.getText());

    }


}