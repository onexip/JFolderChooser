package com.ultramixer.jfolderchooser.demo;

import com.ultramixer.jfolderchooser.DefaultNodeFactory;
import com.ultramixer.jfolderchooser.DirTreeModel;
import com.ultramixer.jfolderchooser.DirTreeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Demo
{


    private JFrame frame;
    private Dimension normalSize = new Dimension(800,600);

    private Demo()
    {


        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTree tree = new JTree();
        DirTreeUtils.configureTree(tree, new DirTreeModel<>(new DefaultNodeFactory()));
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(tree);

        JButton exitbnt = new JButton("Exit");
        exitbnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        JButton test = new JButton("test");
        test.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean isNormal = frame.getExtendedState() == JFrame.NORMAL;
                boolean isUndecorated = frame.isUndecorated();

                if(isNormal && !isUndecorated)
                {
                    normalSize = frame.getSize();
                }

                frame.dispose();
                frame.setUndecorated(isNormal);
                frame.setExtendedState(isNormal ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
                frame.setSize(normalSize);
                frame.setVisible(true);

                System.out.println("IsFullscreen: " + isNormal);

            }
        });

        JButton fullBnt = new JButton("Fullscreen");
        fullBnt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (frame != null)
                {
                    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    if (device.isFullScreenSupported())
                    {
                        frame.dispose();

                        device.setFullScreenWindow(frame);
                    }
                    else
                    {
                        device.setFullScreenWindow(null);
                    }
                    System.out.println("Fullscreen");
                }
            }
        });

        contentPanel.add(exitbnt, BorderLayout.NORTH);
        contentPanel.add(sp, BorderLayout.CENTER);
        contentPanel.add(fullBnt, BorderLayout.SOUTH);
        contentPanel.add(test, BorderLayout.SOUTH);


        frame = new JFrame("Demo");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(contentPanel);
        frame.setSize(normalSize);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);


        frame.addWindowStateListener(new WindowStateListener()
        {
            @Override
            public void windowStateChanged(WindowEvent e)
            {
                System.out.println("Test");
            }
        });


    }


    public void setFullscreen(boolean isFullscreen)
    {
        frame.dispose();
        if(isFullscreen)
        {
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        else
        {
            frame.setUndecorated(false);
            frame.setExtendedState(JFrame.NORMAL);
            normalSize = frame.getSize();
            frame.setSize(normalSize);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Demo::new);
    }
}
