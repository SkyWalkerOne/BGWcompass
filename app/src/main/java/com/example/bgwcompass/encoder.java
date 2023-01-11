package com.example.bgwcompass;

public class encoder {
    private final String list = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefjhijklmnopqrstuvwxyz1234567890";
    private final String key0 = "гдеабвийкёжзопрлмнстучшщфхцэюяъыьdefabcjkljhipqrmnostuyzvwx4561239078";

    public String encode(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            boolean catched = false;
            for (int j = 0; j < list.length(); j++) {
                if (str.charAt(i) == list.charAt(j)) {
                    sb.append(key0.charAt(j));
                    catched = true;
                    break;
                }
            }
            if (!catched) sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    public String decode (String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            boolean catched = false;
            for (int j = 0; j < key0.length(); j++) {
                if (str.charAt(i) == key0.charAt(j)) {
                    sb.append(list.charAt(j));
                    catched = true;
                    break;
                }
            }
            if (!catched) sb.append(str.charAt(i));
        }
        return sb.toString();
    }
}
