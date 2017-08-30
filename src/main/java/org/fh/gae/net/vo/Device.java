package org.fh.gae.net.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    /**
     * 设备标识
     */
    private String id;

    private String mac;

    private String ip;

    /**
     * 设备类型,
     */
    private Integer type;
}
