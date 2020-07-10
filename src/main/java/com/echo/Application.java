package com.echo;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class Application extends DirectoryWalker {

    private Scanner sc = new Scanner(System.in);
    private String extension;
    private int errorTimes = 0;

    public static void main(String[] args) throws IOException {
        new Application("java").run();
    }

    public Application(String extension) {
        super();
        this.extension = extension;
    }

    public void run() throws IOException {
        //删除指定文件夹下（含子文件夹）所有java文件的BOM，若构造器中参数为null则删除所有文件头部BOM
        System.out.println("请输入目标文件夹物理路径：");
        String path = sc.next();
        System.out.println("");

        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            errorTimes++;
            if (errorTimes >= 5) {
                stop("错误次数超过5次，清理结束");
                System.exit(0);
            } else {
                System.out.println("/** 路径不存在，或不是文件夹，请重新输入。 **/");
                System.out.println();
            }
            run();
        } else {
            System.out.println("开始清理：" + dir.getPath());
            System.out.println("----------------------------------------");
            System.out.println("");
            walk(dir, null);
            stop("清理结束");
        }
    }

    protected void handleFile(File file, int depth, Collection results) throws IOException {
        if (extension == null || extension.equalsIgnoreCase(FilenameUtils.getExtension(file.toString()))) {
            //调用具体业务逻辑，其实这里不仅可以实现删除BOM，还可以做很多想干的事情。
            remove(file);
        }
    }

    /**
     * 移除UTF-8的BOM
     */
    private void remove(File file) throws IOException {
        byte[] bs = FileUtils.readFileToByteArray(file);
        if (bs[0] == -17 && bs[1] == -69 && bs[2] == -65) {
            byte[] nbs = new byte[bs.length - 3];
            System.arraycopy(bs, 3, nbs, 0, nbs.length);
            FileUtils.writeByteArrayToFile(file, nbs);
            System.out.println("Remove BOM: " + file);
        }
    }

    /**
     * 任务结束
     *
     * @throws IOException
     */
    private void stop(String info) throws IOException {
        System.out.println("");
        System.out.println("----------------------------------------");
        System.out.println(info + "，按Enter键结束进程。");
        System.in.read();
    }
}