package com.qq.wsd.k8s.interfaces.container;

import com.alibaba.fastjson.JSON;
import com.eju.ess.HttpUtil;
import com.qq.jutil.j4log.Logger;
import com.qq.wsd.itil.annotation.InterfaceConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qq.jutil.j4log.Logger;
import com.qq.wsd.itil.annotation.InterfaceConfig;
import com.qq.wsd.itil.commons.SystemConfigSetting;
import com.qq.wsd.itil.interfaces.BasicInterface;
import com.qq.wsd.k8s.dao.BasicDAO;
import com.qq.wsd.k8s.utils.NoticeUtils;

import javax.management.AttributeList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import javax.swing.text.html.HTML;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

@InterfaceConfig(name = "notification")
public class ContainerNotification extends BasicInterface {

    Logger logger = Logger.getLogger("test1");

    @Override
    protected JSONObject process(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        JSONArray result = BasicDAO.getJSONArrayBySQL("select * from t_testidle_opera");

//            JSONObject dataTemp=result.getJSONObject(1);
////            JSONArray data = dataTemp.getJSONObject("data");
//
//            logger.info(dataTemp.toJSONString());
        String message1;
        String message2;
        String message3;
        String message4;
        ArrayList messages1 = new ArrayList();
        ArrayList messages2 = new ArrayList();
        ArrayList id = new ArrayList();
        ArrayList name = new ArrayList();
        ArrayList container_id = new ArrayList();
        ArrayList container_ip = new ArrayList();
        ArrayList opera_time = new ArrayList();
        ArrayList opera_type = new ArrayList();
        ArrayList creator = new ArrayList();
        ArrayList container_type = new ArrayList();
        String remindTimes;
        remindTimes = (String) SystemConfigSetting.getSystemConfigSettingByKey("idle.remind_times");
        int i = 0;
        while (result.size() > i) {
            id.add(i, result.getJSONObject(i).getString("id"));
            name.add(i, result.getJSONObject(i).getString("container_name"));
            container_id.add(i, result.getJSONObject(i).getString("container_id"));
            container_ip.add(i, result.getJSONObject(i).getString("container_ip"));
            opera_time.add(i, result.getJSONObject(i).getString("opera_time"));
            creator.add(i, result.getJSONObject(i).getString("creator"));
            container_type.add(i, result.getJSONObject(i).getString("container_type"));
            opera_type.add(i, result.getJSONObject(i).getString("opera_type"));

            message1 =
                    "你的 " + name.get(i) + " 容器长期没有使用\n" +
                            "系统在提示{ " + remindTimes + " }次后会回收你的容器\n" +
                            "查看详情：http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_remind_list&creator=" + creator.get(i);
            message2 =
                    "你的 " + name.get(i) + " 容器在系统提示多次后仍未使用，系统已经回收你的容器。\n" +
                            "查看被回收的容器：http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_delete_list_total&creator=" + creator.get(i);
            messages1.add(i, message1);
            messages2.add(i, message2);
            i++;
        }
//        multiple container mail  template:
//【你有容器即将被回收】
//        你有 + tota +台容器长期没有使用
//        系统在提示 + remind_time +次后会回收你的容器
//        查看详情：website_link
//
//
//
//        multiple container delete  template:
//【你的容器已被回收】
//        你有 + total +台在系统提示多次后仍未使用，系统已经回收你的容器。
//        查看被回收的容器：website_link
//
//
//
//        single container mail  template:
//【你有容器即将被回收】
//        你的 + container_name +容器长期没有使用
//        系统在提示 + remind_times +次后会回收你的容器
//        查看详情：website_link
//
//
//
//
//        single container delete  template:
//【你的容器已被回收】
//        你的 + container_name + 容器在系统提示多次后仍未使用，系统已经回收你的容器。
//        查看被回收的容器：website_link
        int collection_num = 0;


        ArrayList check1 = new ArrayList<>();
        ArrayList check2 = new ArrayList();

        int y = 0;
        for (int x = 0; x < i; x++) {

            if (opera_type.get(x).equals("mail")) {
                logger.info((String) opera_type.get(x));
                if (Collections.frequency(creator, creator.get(x)) > 1 && !check1.contains(creator.get(x))) {
                    check1.add(creator.get(x));
                    collection_num = Collections.frequency(creator, creator.get(x));
                    message3 = "你有" + Integer.toString(collection_num) + "台容器长期没有使用 \n 系统在提示" + (String) remindTimes + "次后会回收你的容器 \n " +
                            "查看详情：http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_remind_list&creator=" + creator.get(x);

                    NoticeUtils.sendRtx((String) creator.get(x), message3, "【你有容器即将被回收】\n", null);
                    logger.info(message3);
                } else if (!check1.contains(creator.get(x))) {
                    check1.add(creator.get(x));
                    NoticeUtils.sendRtx((String) creator.get(x), (String) messages1.get(x), "【你有容器即将被回收】\n", null);
                    logger.info((String) messages1.get(x));
                }
            } else if (opera_type.get(x).equals("delete")) {
                logger.info(Integer.toString(Collections.frequency(creator, creator.get(x))));
                logger.info(Boolean.toString(check2.contains(creator.get(x))));
                if (Collections.frequency(creator, creator.get(x)) > 1 && !check2.contains(creator.get(x))) {
                    logger.info("hi world");
                    check2.add(creator.get(x));
                    collection_num = Collections.frequency(creator, creator.get(x));

                    String link = "<a target='_blank' style='color:#4E7CDA;font-size:16px;' href = 'http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_delete_list_total&creator=${'" + creator.get(x) + "}'>请点击查看具体规则</a>)";
                    String link2= "<a href=\"https://www.w3schools.com/html/\">Visit our HTML tutorial</a>";
                    message4 = "你有" + Integer.toString(collection_num)  + "台在系统提示多次后仍未使用，系统已经回收你的容器。\n"
                    "查看被回收的容器：http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_delete_list_total&creator=" + creator.get(x);
//                            "你有 " + + " 台在系统提示多次后仍未使用，系统已经回收你的容器。 \n" +
//                                    "如需使用，请在系统中重新上架，" + "<a target='_blank' style='color:#4E7CDA;font-size:16px;' " +
//                                     "href = 'http://show.wsd.com/show3.htm?viewId=db_k8s.t_md_t_idle_container_delete_list_total&creator='"
//                                    + creator.get(x) + "'>请点击查看具体规则</a>)"+"";

                    NoticeUtils.sendRtx((String) creator.get(x), "hi world" + message4, "【你的容器已被回收】\n ", null);
                    logger.info(message4);
                } else if (!check2.contains(creator.get(x))) {
                    check2.add(creator.get(x));
                    logger.info((String) opera_type.get(x));
                    NoticeUtils.sendRtx((String) creator.get(x), (String) messages2.get(x), "【你的容器已被回收】\n", null);
                    logger.info((String) messages2.get(x));
                }
            }
            y++;
        }


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", messages1.get(0));

        return MakeResObject(EC_SUCCESS, "", jsonObject);

    }
//    public boolean numcheck1(ArrayList t) {
//        Arraylist
//    }
}
