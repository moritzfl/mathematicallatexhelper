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
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import de.moritzf.latexhelper.util.GuiUtil;
import de.moritzf.latexhelper.util.OsUtil;
import mathpix.MathPixSettings;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * This class defines the user interface.
 *
 * @author Moritz Floeter
 */
public class MathematicalLatexHelperGui extends JFrame implements ActionListener, DocumentListener {
    /**
     * The text area holding the latex source code that is to be rendered.
     */
    private JTextArea latexSource;

    private UndoManager undoManager;

    private JMenuItem settingsItem = new JMenuItem("Image Import Settings");

    /**
     * The save button.
     */
    private JButton btnSave = new JButton("Save");

    /**
     * The copy button for pdfs.
     */
    private JButton btnCopyPdf = new JButton("Copy pdf");

    /**
     * The copy button for images.
     */
    private JButton btnCopyImage = new JButton("Copy image");

    /**
     * The drawing area for the rendered result.
     */
    private JLabel drawingArea = new JLabel("");


    private static final Font textAreaFont = new Font("Courier", Font.PLAIN, 16);


    /**
     * Instantiates a new gui.
     */
    public MathematicalLatexHelperGui() {
        super("Mathematical LaTeX Helper");

        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu.
        JMenu fileMenu = new JMenu("File");


        fileMenu.getAccessibleContext().setAccessibleDescription(
                "File menu");
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        fileMenu.add(settingsItem);
        settingsItem.addActionListener(this);

        Container content = this.getContentPane();
        content.setLayout(new GridLayout(2, 1));
        this.latexSource = new LatexImportingTextArea();

        this.latexSource.setFont(textAreaFont);

        initUndoRedoFunctionality();

        JPanel editorArea = new JPanel();
        editorArea.setLayout(new BorderLayout());
        editorArea.add(new JScrollPane(this.latexSource), BorderLayout.CENTER);

        JPanel btnPnl = new JPanel(new GridLayout(1, 0));
        btnPnl.add(btnCopyImage);
        btnPnl.add(btnCopyPdf);
        btnPnl.add(btnSave);
        editorArea.add(btnPnl, BorderLayout.SOUTH);

        content.add(editorArea);
        drawingArea.setBackground(Color.WHITE);
        content.add(new JScrollPane(this.drawingArea));

        // adding Listeners
        latexSource.getDocument().addDocumentListener(this);
        this.btnCopyImage.addActionListener(this);
        this.btnCopyPdf.addActionListener(this);
        this.btnSave.addActionListener(this);

        this.latexSource.setText("x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}");

        this.setSize(500, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        GuiUtil.centerWindow(this);
        if (OsUtil.getOperatingSystemType().equals(OsUtil.OSType.MacOS)) {
            GuiUtil.enableOSXFullscreen(this);
        }

        this.setVisible(true);
    }

    /**
     * Instantiates a the undo-redo functionality for the textarea.
     */
    private void initUndoRedoFunctionality() {
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
    private void render(String expression) {
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
            handleSave();
        } else if (e.getSource().equals(this.btnCopyImage)) {
            try {
                Export.setClipboardAsImage(this.latexSource.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Make sure you entered a valid LaTeX-expression", "Could not copy",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource().equals(this.btnCopyPdf)) {
            try {
                Export.setClipboardAsPdf(this.latexSource.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Make sure you entered a valid LaTeX-expression", "Could not copy",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource().equals(this.settingsItem)) {
            handleSettings();
        }
    }

    private void handleSave() {
        try {
            Path path = Export.save(this.latexSource.getText());

            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    MathematicalLatexHelperGui.this.render(latexSource.getText());
                }
            });
            MathematicalLatexHelperGui.this.render("\\text{Saved to folder: "
                    + path.toAbsolutePath().toString().replace("\\", "}\\backslash \\text{") + " }");
            timer.setRepeats(false); // Only execute once
            timer.start();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this,
                    "<html>Make sure you entered a valid LaTeX-expression.<br>"
                            + " Furthermore ensure that you have writing access to"
                            + " the directory: " + Export.USER_HOME + "<html>",
                    "Could not save", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSettings() {
        JTextField appIdField = new JTextField();
        JTextField apiKeyField = new JTextField();
        JTextField baseUrlField = new JTextField();

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        JPanel descriptionPnl = new JPanel(new BorderLayout());

        descriptionPnl.add(new JLabel(
                "<html>Mathematical LaTeX Helper integrates multiple recognition mechanisms for images:<br>" +
                        "- Formulas rendered by this tool always include the original formula<br>" +
                        "- The tool will always start by trying to find this original formula within imported images<br>" +
                        "- If that fails, the tool will try to apply OCR using MathPix if it is available<br>" +
                        "- If MathPix is not configured or the MathPix servers can't be reached, a java OCR library is used (MathOCR)<br><br>" +
                        "MathPix offers results superior to MathOCR but is not without drawbacks:<br>" +
                        "- only works with an active internet connection<br>" +
                        "- a free developer account allows up to 1000 free queries after which you pay for each query<br>" +
                        "- even for a free account, you have to link your credit card to the account<br><br>" +
                        "<i>If you do not want to create a developer account, you can also use MathPix standalone tool.</i><br>"), BorderLayout.WEST);
        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));

        linkPanel.add(new URLLabel("Create MathPix developer account", "https://dashboard.mathpix.com/login"));
        linkPanel.add(new URLLabel("Download MathPix standalone tool", "https://mathpix.com"));
        descriptionPnl.add(linkPanel, BorderLayout.SOUTH);
        myPanel.add(descriptionPnl);

        myPanel.add(new JLabel(" "));

        Font settingsLabelFont = new Font("Monospaced", Font.PLAIN, 12);
        JLabel appIdLabel = new JLabel("app_id  :");
        JLabel apiKeyLabel = new JLabel("api_key :");
        JLabel baseUrlLabel = new JLabel("base_url:");
        appIdLabel.setFont(settingsLabelFont);
        apiKeyLabel.setFont(settingsLabelFont);
        baseUrlLabel.setFont(settingsLabelFont);

        JPanel appIdPnl = new JPanel(new BorderLayout());
        appIdPnl.add(appIdLabel, BorderLayout.WEST);
        appIdPnl.add(appIdField, BorderLayout.CENTER);
        myPanel.add(appIdPnl);


        JPanel apiKeyPnl = new JPanel(new BorderLayout());
        apiKeyPnl.add(apiKeyLabel, BorderLayout.WEST);
        apiKeyPnl.add(apiKeyField, BorderLayout.CENTER);
        myPanel.add(apiKeyPnl);


        JPanel baseUrlPnl = new JPanel(new BorderLayout());
        baseUrlPnl.add(baseUrlLabel, BorderLayout.WEST);
        baseUrlPnl.add(baseUrlField, BorderLayout.CENTER);
        myPanel.add(baseUrlPnl);


        if (MathPixSettings.isConfigured()) {
            appIdField.setText(MathPixSettings.getAppId());
            apiKeyField.setText(MathPixSettings.getAppKey());
            baseUrlField.setText(MathPixSettings.getBaseUrl());
        } else {
            baseUrlField.setText("https://api.mathpix.com/v3/latex");
        }

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "MathPix Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (!appIdField.getText().isEmpty() && !apiKeyField.getText().isEmpty() && !baseUrlField.getText().isEmpty()) {
                try {
                    MathPixSettings.save(appIdField.getText(), apiKeyField.getText(), baseUrlField.getText());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(this,
                            "Could not save settings. " + e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
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

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        GuiUtil.setSystemWindowDesign();
        // Set system property for more speed as recommended for apache pdfbox
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        MathPixSettings.load();
        new MathematicalLatexHelperGui();
    }


}