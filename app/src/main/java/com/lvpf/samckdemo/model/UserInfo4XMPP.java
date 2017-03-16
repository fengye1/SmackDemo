package com.lvpf.samckdemo.model;


import org.jivesoftware.smack.packet.PacketExtension;

/**
 * 为Message扩展用户信息
 * @author 杨元（转载不注明出处可耻，原文请搜索[杨元博客]）
 * @version 创建时间：2015年5月11日 下午4:55:38
 */
public class UserInfo4XMPP implements PacketExtension {

    //用户信息元素名称
    private String elementName = "userinfo";
    //用户昵称元素名称
    private String nameElement = "name";
    //用户头像地址元素名称
    private String urlElement = "url";
    //用户昵称元素文本(对外开放)
    private String nameText = "";
    //用户头像地址元素文本(对外开放)
    private String urlText = "";

    @Override
    public String getElementName() {
        return elementName;
    }

    /**
     * 返回扩展的xml字符串
     * 此字符串作为message元素的子元素
     */
    @Override
    public String toXML() {
        StringBuilder sb = new StringBuilder();

        sb.append("<");
        sb.append(elementName);
        sb.append(">");

        sb.append("<");
        sb.append(nameElement);
        sb.append(">");
        sb.append(nameText);
        sb.append("</");
        sb.append(nameElement);
        sb.append(">");

        sb.append("<");
        sb.append(urlElement);
        sb.append(">");
        sb.append(urlText);
        sb.append("</");
        sb.append(urlElement);
        sb.append(">");

        sb.append("</");
        sb.append(elementName);
        sb.append(">");

        return sb.toString();
    }

    /**
     * 可忽略
     */
    @Override
    public String getNamespace() {
        return "";
    }

    public String getNameText() {
        return nameText;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public String getUrlText() {
        return urlText;
    }

    public void setUrlText(String urlText) {
        this.urlText = urlText;
    }
}