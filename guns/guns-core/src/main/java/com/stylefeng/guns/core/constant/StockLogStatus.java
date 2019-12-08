package com.stylefeng.guns.core.constant;

/**
 * @author: jia.xue
 * @create: 2019-08-14 11:29
 * @Description
 **/
public class StockLogStatus extends BaseType {
    private static final long serialVersionUID = -3040974173679297639L;

    protected StockLogStatus(Integer index, String description) {
        super(index, description);
    }

    public static StockLogStatus INIT = new StockLogStatus(1,"初始化");
    public static StockLogStatus SUCCESS = new StockLogStatus(2,"成功");
    public static StockLogStatus FAIL = new StockLogStatus(3,"失败");

}