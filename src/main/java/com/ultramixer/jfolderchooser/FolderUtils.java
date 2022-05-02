package com.ultramixer.jfolderchooser;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FolderUtils
{
    public static FolderUtils instance;
    Font awesomeFont_12plain;

    private FolderUtils()
    {
        InputStream in = FolderUtils.class.getClassLoader().getResourceAsStream("fa-solid-900.ttf");
        try
        {
            awesomeFont_12plain = Font.createFont(Font.TRUETYPE_FONT, in);
            awesomeFont_12plain = awesomeFont_12plain.deriveFont(Font.PLAIN, 12f);
        } catch (FontFormatException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized static FolderUtils getInstance()
    {
        if (instance == null)
        {
            instance = new FolderUtils();
        }
        return instance;
    }

    public Font getAwesomeFont_12plain()
    {
        return awesomeFont_12plain;
    }

    public void enableOSXFullscreen(Window window) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void requestOSXFullscreen(Window window) {
        try {
            Class appClass = Class.forName("com.apple.eawt.Application");
            Class params[] = new Class[]{};

            Method getApplication = appClass.getMethod("getApplication", params);
            Object application = getApplication.invoke(appClass);
            Method requestToggleFulLScreen = application.getClass().getMethod("requestToggleFullScreen", Window.class);

            requestToggleFulLScreen.invoke(application, window);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public void enableFullScreenMode(Window window)
    {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";

        try
        {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[]{
                    Window.class, boolean.class});
            method.invoke(null, window, true);
        } catch (Throwable t)
        {
            System.err.println("Full screen mode is not supported");
            t.printStackTrace();
        }
    }
}
