package com.hsp.huffmancode;

import com.beust.jcommander.IVariableArity;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

/**
 * @author 侯旭~
 * @version 1.0
 */
public class HuffmanCodeDemo {
    public static void main(String[] args) {
//        //String str = "i like like like java do you like a java";
//        String str = "change the subject of conversation; switch the conversation to another subject";
//        byte[] bytes = zip(str.getBytes());
//        //System.out.println(Arrays.toString(bytes));
//        byte[] decode = decode(table, bytes);
//        for(byte item:decode){
//            System.out.print((char)item);
//        }
        //测试压缩文件
//        String srcFile = "F:/a.txt";
//        String dstFile = "F:/a.zip";
//        zipFile(srcFile,dstFile);

        //测试解压文件
        String zipFile = "F:/a.zip";
        String dstFile = "F:/a2.txt";
        unZipFile(zipFile,dstFile);
        System.out.println("解压成功");

    }
    //编写方法进行压缩

    /**
     *
     * @param srcFile 源文件
     * @param dstFile 压缩文件位置
     */
    public static void zipFile(String srcFile,String dstFile){
        //创建输出流
        FileOutputStream os=null;
        ObjectOutputStream oos = null;
        //创建输入流
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            byte [] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);
            byte[] huffmanCode = zip(b);
            os = new FileOutputStream(dstFile);
            oos = new ObjectOutputStream(os);
            oos.writeObject(huffmanCode);
            oos.writeObject(table);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                fileInputStream.close();
                os.close();
                oos.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    //编写方法解压缩
    public static void unZipFile(String zipFile,String dstFile){
        //创建文件输入流
        InputStream is = null;
        ObjectInputStream oos = null;
        //创建文件输出流
        OutputStream os = null;
        try {
            is = new FileInputStream(zipFile);
            oos = new ObjectInputStream(is);
            byte[] huffmanCode =(byte[]) oos.readObject();
            Map<Byte,String> map = (Map<Byte,String>) oos.readObject();
            byte[] decode = decode(map, huffmanCode);
            os = new FileOutputStream(dstFile);
            os.write(decode);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if(is!=null){
                    is.close();
                }
                if(oos!=null){
                    oos.close();
                }
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    //得到哈夫曼编码byte数组转成原始数组
    public static byte[] decode(Map<Byte,String> huffmanCode,byte [] huffmanBytes){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<huffmanBytes.length;i++){
            boolean flag = (i==huffmanBytes.length-1);
            stringBuilder.append(byteToBinary(!flag,huffmanBytes[i]));
        }
        HashMap<String, Byte> map = new HashMap<>();
        for(Map.Entry<Byte,String> entry:huffmanCode.entrySet()){
            map.put(entry.getValue(),entry.getKey());
        }
        //System.out.println(map);
        StringBuilder temp = new StringBuilder();
        List<Byte>list = new ArrayList<>();
        //System.out.println(stringBuilder);
        //方法一
        for(int i =0;i<stringBuilder.length();i++){
            temp.append(stringBuilder.charAt(i));
            //System.out.println("temp="+temp);
            if(map.containsKey(temp.toString())){
                list.add(map.get(temp.toString()));
                temp.replace(0,temp.length(),"");
            }
        }
//        //方法二
//        for(int i =0;i<stringBuilder.length(); ){
//            int count=1;
//            boolean flag = true;
//            Byte b = null;
//            while(flag){
//                String key = stringBuilder.substring(i,i+count);
//                b = map.get(key);
//                if(b==null){
//                    count++;
//                }else{
//                    flag = false;
//                }
//            }
//            list.add(b);
//            i+=count;
//        }

        //System.out.println(list);
        int len =list.size();
        byte[] bytes = new byte[len];
        int i =0;
        for(byte item:list){
            bytes[i] = item;
            i++;
        }
        return bytes;

    }
    //将一个字节byte转成对应的二进制补码
    public static String byteToBinary(boolean flag,byte b){
        int temp = b;
        if(flag){
            temp|=256;
        }
        String str = Integer.toBinaryString(temp);//返回temp对应的二进制补码
        if(flag){
            return str.substring(str.length()-8);
        }else{
            return str;
        }
    }

    //  得到哈夫曼编码
    public static byte[] zip(byte[] bytes) {

        //生成哈夫曼树
        Node huffmanTree = createHuffmanTree(bytes);
        //得到哈夫曼编码
        getCode(huffmanTree);

        StringBuilder sb = new StringBuilder();
        //得到对应二进制数
        for (byte item : bytes) {
            sb.append(table.get(item));
        }
        //System.out.println(sb.toString());
        //哈夫曼字节数组的长度
//        int len = (sb.length()+7)/8;
        int len;
        if (sb.length() % 8 == 0) {
            len = sb.length() / 8;
        } else {
            len = sb.length() / 8 + 1;
        }
        byte[] huffmanCodeBytes = new byte[len];
        int index = 0;
        for (int i = 0; i < sb.length(); i += 8) {
            String s;
            if (i + 8 > sb.length()) {
                s = sb.substring(i);
            } else {
                s = sb.substring(i, i + 8);
            }
            //System.out.println(s.toString());
            huffmanCodeBytes[index] = (byte) Integer.parseInt(s, 2);
            index++;
        }
        return huffmanCodeBytes;

    }

    //  生成霍夫曼树
    public static Node createHuffmanTree(byte[] chars) {

        Map<Byte, Integer> mp = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            mp.put(chars[i], mp.getOrDefault(chars[i], 0) + 1);
        }
        List<Node> nodes = new ArrayList<>();
        Set<Byte> set = mp.keySet();
        for (Byte item : set) {
            nodes.add(new Node(item, mp.get(item)));
        }
        //System.out.println("nodes=" + nodes);
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            Node leftNode = nodes.get(0);
            Node rightNode = nodes.get(1);
            Node parent = new Node(null, leftNode.weight + rightNode.weight);
            parent.left = leftNode;
            parent.right = rightNode;
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            nodes.add(parent);
        }
        return nodes.get(0);
    }

    //找到每一个叶子结点
    static HashMap<Byte, String> table = new HashMap<>();
    static StringBuilder stringBuilder = new StringBuilder();

    private static Map<Byte, String> getCode(Node node) {
        if (node == null) {
            return null;
        }
        getCode(node.left, "0", stringBuilder);
        getCode(node.right, "1", stringBuilder);
        return table;
    }

    /**
     * 功能：将传入的node结点的所有叶子结点的哈夫曼编码得到并存人table中
     *
     * @param node          传入结点
     * @param code          路径，左节点是0，右节点是1
     * @param stringBuilder 用于拼接路径
     * @return
     */
    private static void getCode(Node node, String code, StringBuilder stringBuilder) {
        StringBuilder stringBuilder2 = new StringBuilder(stringBuilder);
        stringBuilder2.append(code);
        if (node != null) {
            if (node.date == null) {
                getCode(node.left, "0", stringBuilder2);
                getCode(node.right, "1", stringBuilder2);
            } else {
                table.put(node.date, stringBuilder2.toString());
            }
        }

    }


    public static void preOrder(Node root) {
        if (root != null) {
            root.preOrder();
        } else {
            System.out.println("此霍夫曼树为空");
        }
    }
//    @Test
//    public static Map<Integer,Integer> transformStringToHashMap(String str){
//        char[] chars = str.toCharArray();
//        Map<Integer,Integer> mp = new HashMap<>();
//        for(int i = 0;i<chars.length;i++){
//            mp.put((int)chars[i],mp.getOrDefault((int)chars[i],0)+1);
//        }
//        return mp;
//
//    }
}

class Node implements Comparable<Node> {
    Byte date;
    int weight;
    Node left;
    Node right;

    public Node(Byte date, int weight) {
        this.date = date;
        this.weight = weight;
    }

    //前序遍历
    public void preOrder() {
        System.out.println(this.weight);
        if (this.left != null) {
            this.left.preOrder();
        }
        if (this.right != null) {
            this.right.preOrder();
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "date=" + date +
                ", value=" + weight +
                '}';
    }

    @Override
    public int compareTo(Node o) {
        return this.weight - o.weight;
    }
}
