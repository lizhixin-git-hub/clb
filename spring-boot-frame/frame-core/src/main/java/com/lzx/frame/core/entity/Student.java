package com.lzx.frame.core.entity;

import net.coobird.thumbnailator.Thumbnails;

import java.io.*;
import java.util.stream.Stream;

public class Student {

    private String name;

    private String no;

    private String email;

    private String phone;

    private Integer score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", score=" + score +
                '}';
    }

    private static void test(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                Stream.of(files).forEach(f -> {
                    if (f.isDirectory()) {
                        test(f.getAbsolutePath());
                    } else {
                        try {
                            InputStream inputStream = new FileInputStream(f);
                            Thumbnails.of(inputStream).scale(1f).outputQuality(0.1f).toFile(f.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        test("C:\\Users\\Administrator\\Desktop\\zqvtspimg\\vtsp_imgs\\");
    }
}
