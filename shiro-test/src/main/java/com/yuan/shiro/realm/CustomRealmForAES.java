package com.yuan.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangy on 2018/10/14.
 * <p>
 * shiro aes算法加密:
 * 将数据库密码解密，放入主体认证信息
 */
public class CustomRealmForAES extends AuthorizingRealm {

    Map<String, String> userMap = new HashMap<>(16);

    {
        //初始化一些测试数据
        userMap.put("wangyuan", "960ce2fa82f1c796185ece965b8e4ec8");
        super.setName("customRealm");
    }

    //模拟去数据库查询出的结果
    private Set<String> getRolesByUsername(String username) {
        //假设用户有admin，以及user两个角色
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    //模拟去数据库查询出的结果
    private Set<String> getPermissionsByUsername(String username) {
        //假设角色有del，以及add两个权限
        Set<String> sets = new HashSet<>();
        sets.add("del");
        sets.add("add");
        return sets;
    }

    //模拟去数据库查询出的结果
    private String getPwdByUsername(String username) {
        return userMap.get(username);
    }

    /**
     * 该方法是用来做授权用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1，获取传来主体的认证信息中的用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        //2，从数据库或缓存中获取角色数据
        Set<String> roles = getRolesByUsername(username);
        //3，从数据库或缓存中获取角色的权限数据
        Set<String> permissions = getPermissionsByUsername(username);
        //4，将查询出的数据返回
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    /**
     * 该方法是用来做认证用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        //1，获取传来主体的认证信息中的用户名
        String username = (String) authenticationToken.getPrincipal();
        //2，通过用户名去数据库中获取凭证（也就是密码）
        String pwd = getPwdByUsername(username);//密文
        if (pwd == null) {
            return null;
        }
        String password = CustomRealmForAES.decrypt(pwd);//明文
        //3，将查询出的数据返回
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo
                ("wangyuan", password, "customRealm");
        return authenticationInfo;
    }

    public static void main(String[] args) throws Exception {
        String pwd = "666666";
        System.out.println(encrypt(pwd));
    }

    public static final String logalrithm = "AES/CBC/PKCS5Padding";
    public static final String bm = "utf-8";
    private static byte[] keyValue = new byte[]{
            22, -35, -45, 25, 98, -55, -45, 10, 35, -45, 25, 26, -95, 25, -35, 48
    };
    private static byte[] iv = new byte[]{
            -12, 35, -25, 65, 45, -87, 95, -22, -15, 45, 55, -66, 32, 5 - 4, 84, 55
    };

    private static Key keySpec;
    private static IvParameterSpec ivSpec;

    static {
        keySpec = new SecretKeySpec(keyValue, "AES");
        ivSpec = new IvParameterSpec(iv);
    }

    /**
     * @param msg 加密的数据
     * @return
     * @throws
     * @Title: encrypt
     * @Description: 加密，使用指定数据源生成密钥，使用用户数据作为算法参数进行AES加密
     * @date 2015-9-23 上午9:09:20
     */
    public static String encrypt(String msg) {
        byte[] encryptedData = null;
        try {
            Cipher ecipher = Cipher.getInstance(logalrithm);
            ecipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encryptedData = ecipher.doFinal(msg.getBytes(bm));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asHex(encryptedData);
    }

    /**
     * @param value
     * @return
     * @throws
     * @Title: decrypt
     * @Description: 解密，对生成的16进制的字符串进行解密
     * @date 2015-9-23 上午9:10:01
     */
    public static String decrypt(String value) {
        try {
            Cipher ecipher = Cipher.getInstance(logalrithm);
            ecipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(ecipher.doFinal(asBin(value)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param buf
     * @return
     * @throws
     * @Title: asHex
     * @Description: 将字节数组转换成16进制字符串
     * @date 2015-9-23 上午9:10:25
     */
    private static String asHex(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    /**
     * @param src
     * @return
     * @throws
     * @Title: asBin
     * @Description: 将16进制字符串转换成字节数组
     * @date 2015-9-23 上午9:10:52
     */
    private static byte[] asBin(String src) {
        if (src.length() < 1) {
            return null;
        }
        byte[] encrypted = new byte[src.length() / 2];
        for (int i = 0; i < src.length() / 2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }

}
