package com.ultramixer.jfolderchooser;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JFolderChooser
{
    public static JFolderChooser instance;
    private JFrame window = null;
    private JPanel contentPanel;
    private JTree tree;
    private DirTreeModel model;
    private JScrollPane sp;
    private JButton cancelButton;
    private JButton addButton;
    private JCheckBox cb_includeSubDirs;
    private JCheckBox cb_createGroups;
    private JComboBox comb_RecentFiles;
    private List<Path> selectionFilePaths = new ArrayList<>();
    private List<String> recentFileList = new ArrayList<>();
    private AbstractAction disposeAction;

    private String dialogTitle = "FolderChooser";
    private String cancelButtonText = "Cancel";
    private String approveButtonText = "Add...";
    private String includeSubDirsTitle = "Include Subfolders";
    private String createGroupsTitle = "Create Separate Groups";
    private boolean isRecentVisible = false;

    private JFolderChooser(ConfigParameter parameter)
    {
        setDialogTitle(parameter.dialogTitle);
        setApproveButtonText(parameter.approveButtonText);
        setIncludeSubDirsTitle(parameter.includeSubDirsTitle);
        setCreateGroupsTitle(parameter.createGroupsTitle);
        setRecentVisible(parameter.recentVisible);
        open();
    }

    private void open()
    {
        createGUI();
    }


    public void reload()
    {
        Path path = getSelectionPath();
        if(path == null) return;

        this.model = new DirTreeModel<>(DirTreeUtils.NAME_COMPARATOR, false, false, new DefaultNodeFactory());
        tree.clearSelection();
        tree.setModel(model);
        tree.expandPath(model.getFileSystemNodeTreePath());
        setSelection(path);
    }

    private void reloadParent()
    {
        Path path = getSelectionPath();
        if(path == null) return;

        this.model = new DirTreeModel<>(DirTreeUtils.NAME_COMPARATOR, false, false, new DefaultNodeFactory());
        tree.clearSelection();
        tree.setModel(model);
        tree.expandPath(model.getFileSystemNodeTreePath());
        setSelection(path.getParent());
    }

    private boolean setSelection(Path path)
    {
        boolean isPresent = model.getTreePath(path).isPresent();
        if (isPresent)
        {
            TreePath tp = (TreePath) model.getTreePath(path).get();
            tree.setSelectionPath(tp);
            tree.expandPath(tp);
            tree.scrollPathToVisible(tp);
        }
        return isPresent;
    }

    private Path getSelectionPath()
    {
        Path path = null;
        TreePath select = tree.getSelectionModel().getSelectionPath();
        if(select == null) return path;
        Object last = select.getLastPathComponent();
        if (last instanceof DefaultDirNode)
        {
            DefaultDirNode node = (DefaultDirNode) last;
            path = node.getDirectory();
        }
        return path;
    }

    private Path getFilePath(TreePath treePath)
    {
        Path path = null;
        Object last = treePath.getLastPathComponent();
        if (last instanceof DefaultDirNode)
        {
            DefaultDirNode node = (DefaultDirNode) last;
            path = node.getDirectory();
        }
        return path;
    }

    public void createGUI()
    {
        Dimension size = new Dimension(40, 40);
        this.tree = new JTree();
        this.model = new DirTreeModel<>(DirTreeUtils.NAME_COMPARATOR, false, false, new DefaultNodeFactory());


        this.contentPanel = new JPanel(new MigLayout("inset 10", "[fill,grow]", "[fill,grow][fill,grow][][]"));
        DirTreeUtils.configureTree(tree, model);
        this.sp = new JScrollPane();
        sp.setViewportView(tree);

        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                if (e.getSource() instanceof JTree)
                {
                    JTree sourceTree = (JTree) e.getSource();
                    TreePath[] selectionTreePaths = sourceTree.getSelectionModel().getSelectionPaths();
                    selectionFilePaths.clear();
                    for (TreePath selectionTreePath : selectionTreePaths)
                    {
                        Path path = getFilePath(selectionTreePath);
                        if (path != null)
                            selectionFilePaths.add(path);
                    }
                }
            }
        });

        JPanel tbar = new JPanel();
        tbar.setLayout(new MigLayout("inset 0,gap 0,hidemode 2", "0:10:push[]2![][][][][]0:10:push", "[]"));

        JButton homeBnt = new JButton("\ue065");
        homeBnt.setFont(FolderUtils.getInstance().awesomeFont_12plain);
        homeBnt.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        homeBnt.setMinimumSize(size);
        homeBnt.setMaximumSize(size);

        homeBnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String homedir = System.getProperty("user.home");
                Path path = Paths.get(homedir);
                setSelection(path);
            }
        });

        JButton deleteBnt = new JButton("\uf65d");
        deleteBnt.setFont(FolderUtils.getInstance().awesomeFont_12plain);
        deleteBnt.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        deleteBnt.setMinimumSize(size);
        deleteBnt.setMaximumSize(size);

        this.disposeAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.dispose();
            }
        };

        deleteBnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               Path deleteDir = getSelectionPath();
               if(deleteDir != null)
               {
                  int result = JOptionPane.showConfirmDialog(window,"Are you sure want to delete this directory?","Confirm",JOptionPane.YES_NO_OPTION);
                   if(result == JOptionPane.YES_OPTION)
                   { try
                       {
                           if (Files.deleteIfExists(deleteDir))
                           {
                               reloadParent();
                           }
                       }
                       catch (DirectoryNotEmptyException ex)
                       {
                           try
                           {
                               Files.walk(deleteDir).map(Path::toFile).forEach(File::delete);
                               reloadParent();
                           } catch (IOException ioException)
                           {
                               ioException.printStackTrace();
                           }
                       }
                       catch (IOException ioException)
                       {
                           ioException.printStackTrace();
                       }
                   }
               }
            }
        });

        JButton addBnt = new JButton("\uf65e");
        addBnt.setFont(FolderUtils.getInstance().awesomeFont_12plain);
        addBnt.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        addBnt.setMinimumSize(size);
        addBnt.setMaximumSize(size);

        addBnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String newDirName = JOptionPane.showInputDialog(window,"enter directory name?","new Folder");
                if(newDirName!= null && !newDirName.isEmpty())
                {
                    Path parentDir = getSelectionPath();
                    if(parentDir != null)
                    {
                       Path newFolderPath = parentDir.resolve(newDirName);
                        try
                        {
                            Files.createDirectory(newFolderPath);
                            reload();
                        } catch (IOException ioException)
                        {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        });

        JButton updateBnt = new JButton("\uf021");
        updateBnt.setFont(FolderUtils.getInstance().awesomeFont_12plain);
        updateBnt.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        updateBnt.setMinimumSize(size);
        updateBnt.setMaximumSize(size);

        updateBnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                reload();
            }
        });

        this.comb_RecentFiles = new JComboBox<>(recentFileList.toArray(new String[recentFileList.size()]));
        JLabel l_recent = new JLabel("Recent: ");

        l_recent.setVisible(isRecentVisible);
        this.comb_RecentFiles.setVisible(isRecentVisible);

        tbar.add(l_recent, "cell 0 0");
        tbar.add(comb_RecentFiles, "cell 1 0");
        tbar.add(homeBnt, "cell 2 0");
        tbar.add(deleteBnt, "cell 3 0");
        tbar.add(addBnt, "cell 4 0");
        tbar.add(updateBnt, "cell 5 0");

        contentPanel.add(tbar, "cell 0 0");
        contentPanel.add(sp, "cell 0 1");

        this.cancelButton = new JButton(cancelButtonText);
        this.cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.dispose();
            }
        });
        this.addButton = new JButton(approveButtonText);

        this.addButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                window.dispose();
            }
        });

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new MigLayout("inset 10", "10:20:push[]10![]10:20:push", "[]"));
        buttonpanel.add(cancelButton, "cell 0 0");
        buttonpanel.add(addButton, "cell 1 0");

        cb_includeSubDirs = new JCheckBox(includeSubDirsTitle);
        cb_createGroups = new JCheckBox(createGroupsTitle);

        contentPanel.add(buttonpanel, "cell 0 2");
        contentPanel.add(cb_includeSubDirs, "cell 0 3");
        contentPanel.add(cb_createGroups, "cell 0 4");

        this.window = new JFrame(dialogTitle);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setContentPane(contentPanel);
        this.window.setSize(350, 500);
        this.window.setLocationByPlatform(true);
        this.window.setVisible(true);

    }

    public synchronized static JFolderChooser getInstance(ConfigParameter parameter)
    {
        if (instance == null)
        {
            instance = new JFolderChooser(parameter);
        }

        if (!instance.window.isVisible())
            instance.window.setVisible(true);

        return instance;
    }

    public void removeAllActionsFromAddButton()
    {
        for (ActionListener al : this.addButton.getActionListeners())
        {
            this.addButton.removeActionListener(al);
        }
        this.addButton.addActionListener(disposeAction);
    }


    public JButton getAddButton()
    {
        return addButton;
    }

    public List<Path> getSelectionFilePaths()
    {
        return selectionFilePaths;
    }

    public void setRecentVisible(boolean recentVisible)
    {
        isRecentVisible = recentVisible;
    }

    public void setDialogTitle(String dialogTitle)
    {
        this.dialogTitle = dialogTitle;
    }

    public void setApproveButtonText(String approveButtonText)
    {
        this.approveButtonText = approveButtonText;
    }

    public void setCancelButtonText(String cancelButtonText)
    {
        this.cancelButtonText = cancelButtonText;
    }

    public void setIncludeSubDirsTitle(String includeSubDirsTitle)
    {
        this.includeSubDirsTitle = includeSubDirsTitle;
    }

    public void setCreateGroupsTitle(String createGroupsTitle)
    {
        this.createGroupsTitle = createGroupsTitle;
    }


    public static class ConfigParameter
    {
        public String dialogTitle;
        public String approveButtonText;
        public String includeSubDirsTitle;
        public String createGroupsTitle;
        public boolean recentVisible;
    }
}
