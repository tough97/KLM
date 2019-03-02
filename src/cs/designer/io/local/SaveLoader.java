package cs.designer.io.local;


import java.io.*;
import java.net.URL;

public abstract class SaveLoader extends Thread{
    protected File file;
    protected Object object;

    public SaveLoader(final File file) {
        this.file = file;
    }

    public SaveLoader() {
    }

    public abstract Object getObject();

    public abstract void setObject(Serializable object);

    protected Object loadObject() {
        Object loadObject = null;
        if (this.file != null) {
            try {
                final ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(this.file));
                loadObject = ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {

            }
        }
        return loadObject;
    }

    protected void saveObject(final Serializable object) {
        if (this.file != null) {
            try {
                final ObjectOutputStream oos =
                        new ObjectOutputStream(new FileOutputStream(this.file));
                oos.writeObject(object);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
