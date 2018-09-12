package nl.knokko.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.lwjgl.LWJGLUtil;

public class Natives
{
    private static File tmp;
    
    public static void prepare(){
            tmp = new File("natives");
            tmp.mkdir();
            if(LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_WINDOWS){
                extractNative("windows/lwjgl64.dll", tmp);
                extractNative("windows/OpenAL64.dll", tmp);
                extractNative("windows/jinput-dx8_64.dll", tmp);
                extractNative("windows/jinput-raw_64.dll", tmp);
                extractNative("windows/lwjgl.dll", tmp);
                extractNative("windows/OpenAL32.dll", tmp);
                extractNative("windows/jinput-dx8.dll", tmp);
                extractNative("windows/jinput-raw.dll", tmp);
            }
            else if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX){
                extractNative("linux/liblwjgl64.so", tmp);
                extractNative("linux/libopenal64.so", tmp);
                extractNative("linux/libjinput-linux64.so", tmp);
                extractNative("linux/liblwjgl.so", tmp);
                extractNative("linux/libopenal.so", tmp);
                extractNative("linux/libjinput-linux.so", tmp);
            }
            else if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX){
                extractNative("macosx/liblwjgl.jnilib", tmp);
                extractNative("macosx/openal.dylib", tmp);
                extractNative("macosx/libjinput-osx.jnilib", tmp);
            }
            else
                throw new RuntimeException("Unsupported platform: " + LWJGLUtil.getPlatformName());
            System.setProperty("org.lwjgl.librarypath", tmp.getAbsolutePath());
    }

    private static void extractNative(String path, File dir){
        InputStream is = Natives.class.getClassLoader().getResourceAsStream("natives/" + path);
        String[] parts = ("natives/" + path).replaceAll("\\\\", "/").split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
        try {
            File tmp = new File(dir, filename);
            tmp.deleteOnExit();
            FileOutputStream os = new FileOutputStream(tmp);
            byte[] buffer = new byte[1024];
            int readBytes;
            try {
                while ((readBytes = is.read(buffer)) != -1){
                    os.write(buffer, 0, readBytes);
                }
            }
            finally {
                os.close();
                is.close();
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to extract native file " + dir.getAbsolutePath() + "/" + path, e);
        }
    }
}
