package com.ultramixer.jfolderchooser.demo;

import net.miginfocom.swing.MigLayout;
import com.ultramixer.jfolderchooser.DefaultDirNode;
import com.ultramixer.jfolderchooser.DefaultNodeFactory;
import com.ultramixer.jfolderchooser.DirTreeModel;
import com.ultramixer.jfolderchooser.DirTreeUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UMDemo
{
    private UMDemo() {
        JPanel contentPanel = new JPanel(new MigLayout("inset 10","[fill,grow]","[fill,grow][][]"));


        JTree tree = new JTree();



        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                System.out.println(e.getNewLeadSelectionPath().toString());
                TreePath tp = e.getNewLeadSelectionPath();
                if (tp != null) {
                    Object filePathToAdd = tp.getLastPathComponent();
                    System.out.println(filePathToAdd);
                    if (filePathToAdd instanceof DefaultDirNode) {
                        DefaultDirNode node = (DefaultDirNode) filePathToAdd;
                        Path path = node.getDirectory();
                        System.out.println(path.toString());
                    }
                }
            }
        });

        DirTreeModel model = new DirTreeModel<>(new DefaultNodeFactory());

        DirTreeUtils.configureTree(tree, model);
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(tree);
        contentPanel.add(sp, "cell 0 0");

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new MigLayout("inset 10","10:push[]10![]10:push","[]"));
        JButton cancel = new JButton("Cancel");
        JButton add = new JButton("Hinzufügen");
        buttonpanel.add(cancel, "cell 0 0");
        buttonpanel.add(add, "cell 1 0");

        contentPanel.add(buttonpanel, "cell 0 1");
        contentPanel.add(new JCheckBox("Unterverzeichnis einschließen"), "cell 0 2");
        contentPanel.add(new JCheckBox("Für jedes Verzeichnis eine Grupper erzeugen"), "cell 0 3");

        JFrame frame = new JFrame("UMDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(contentPanel);
        frame.setSize(350, 500);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        String homedir = System.getProperty("user.dir");
        Path path= Paths.get(homedir);

        if(model.getTreePath(path).isPresent())
        {
            TreePath tp = (TreePath) model.getTreePath(path).get();
            tree.setSelectionPath(tp);
            tree.scrollPathToVisible(tp);
        }



    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UMDemo::new);
    }
}
