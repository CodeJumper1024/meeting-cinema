package com.stylefeng.guns.rest.service.impl;
import java.util.Date;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.alipay.AlipayService;
import com.stylefeng.guns.rest.alipay.vo.GetPayResultVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.order.OrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = AlipayService.class)
public class AlipayServiceImpl implements AlipayService{

    @Reference(interfaceClass = OrderService.class, check = false, timeout = 5000)
    OrderService orderService;

    @Reference(interfaceClass = CinemaService.class, check = false)
    CinemaService cinemaService;
    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    private static Log log = LogFactory.getLog(AlipayServiceImpl.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService   tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    @Override
    public BaseReqVo getPayInfo(String orderId) {

        String outTradeNo = "tradeprecreate" + orderId;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        int cinemaId = orderService.getCinemaIdbyOrderId(orderId);

        String cinemaName = cinemaService.getCinemaNameById(cinemaId);
        String subject = cinemaName + "影院门店当面付扫码消费";

        // (必填) 订单总金额
        double orderPrice = orderService.getOrderPriceById(orderId);
        String totalAmount = String.valueOf(orderPrice);

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "电影票价共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        //影院id
        String storeId = String.valueOf(cinemaId);

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        //GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        //goodsDetailList.add(goods1);


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                // .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        File file = new File("D:\\meeting\\qrcode");
        if(!file.exists()){
            file.mkdirs();
        }
        int status = 0;
        String filePath = null;
        String key = null;
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径  生成二维码
                // key是文件名
                key = String.format("qr-%s.png", response.getOutTradeNo());
                filePath = String.format("D:\\zfb\\qr-%s.png", response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                status = 0;
                break;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                status = 1;
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                status = 999;
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                status = 999;
                break;
        }

        BaseReqVo baseReqVo = new BaseReqVo<>();
        if(status == 0){
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("orderId",orderId);
            dataMap.put("qRCodeAddress","qRCode/" + key);
            baseReqVo.setStatus(0);
            baseReqVo.setImgPre(null);
            baseReqVo.setData(dataMap);
        }else if(status == 1){
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("订单支付失败，请稍后重试");
        }else{
            //status == 999
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
        }
        return baseReqVo;

    }

    @Override
    public GetPayResultVo getPayResult(String orderId) {
        MoocOrderT moocOrderT=new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);
        GetPayResultVo getPayResultVo = new GetPayResultVo();
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = "tradeprecreate" + orderId;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                dumpResponse(response);

                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                Integer result1 = moocOrderTMapper.updateStateById(orderId);
                if(result1>0){
                    getPayResultVo.setOrderId(orderId);
                    getPayResultVo.setOrderStatus(moocOrderT.getOrderStatus());
                    getPayResultVo.setOrderMsg("支付成功");
                }else{
                    getPayResultVo.setOrderId(orderId);
                    getPayResultVo.setOrderStatus(moocOrderT.getOrderStatus());
                    getPayResultVo.setOrderMsg("支付失败");
                }

                break;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                getPayResultVo.setOrderId(orderId);
                getPayResultVo.setOrderStatus(0);
                getPayResultVo.setOrderMsg("支付失败");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                getPayResultVo.setOrderId(orderId);
                getPayResultVo.setOrderStatus(0);
                getPayResultVo.setOrderMsg("支付失败");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                getPayResultVo.setOrderId(orderId);
                getPayResultVo.setOrderStatus(0);
                getPayResultVo.setOrderMsg("支付失败");
                break;
        }
        return getPayResultVo;
    }

    @Override
    public GetPayResultVo updateFail(String orderId) {
        MoocOrderT moocOrderT=new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);
        GetPayResultVo getPayResultVo = new GetPayResultVo();
        moocOrderTMapper.updateById(moocOrderT);
        return getPayResultVo;
    }

}
