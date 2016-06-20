package com.xiaoyu.ttweather.model;

/**FiveDaysWeather模型类
 * Created by xiaoy on 16/6/8.
 */
public class FiveDaysWeather {

    public FiveDaysWeather(String text, String date, int iconHighTemp,
                           String textHighTemp, String textLowTemp, int iconLowTemp,
                           String windDirection, String windPower) {
        this.text = text;
        this.date = date;
        this.iconHighTemp = iconHighTemp;
        this.textHighTemp = textHighTemp;
        this.textLowTemp = textLowTemp;
        this.iconLowTemp = iconLowTemp;
        this.windDirection = windDirection;
        this.windPower = windPower;
    }

    public String text;
    public String date;
    public int iconHighTemp;
    public String textHighTemp;
    public String textLowTemp;
    public int iconLowTemp;
    public String windDirection;
    public String windPower;
}
