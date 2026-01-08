package site.aronnax.service;

import org.springframework.stereotype.Service;

@Service
public class AIService {

    // Placeholder for actual API integration
    // private static final String API_KEY = "YOUR_DASHSCOPE_API_KEY";

    public String chat(String userMessage) {
        // Simple rule-based responses for demo purposes since we don't have a live API
        // key
        if (userMessage.contains("缴费")) {
            return "您可以在【费用管理】或者【我的钱包】中进行缴费。支持微信、支付宝及余额支付。如果您的余额不足，请先充值。";
        } else if (userMessage.contains("报修")) {
            return "报修请拨打物业热线 8888-1234，或者在前台填写报修单。我们将尽快安排维修师傅上门。";
        } else if (userMessage.contains("水电")) {
            return "水电充值请前往【水电卡管理】页面。请注意，如果您有未缴的物业费，系统可能会限制您的购电功能，请优先结清账单。";
        } else if (userMessage.contains("欠费")) {
            // In a real integration, we could query the DB here and inject context
            return "欠费会导致您的水电卡无法充值。您可以点击首页的【欠费查询】查看具体欠费项。";
        }

        return "我是您的智能物业助手。您可以问我关于缴费、报修、停车等问题。(目前为演示模式，接入通义千问API后将更加智能)";
    }
}
