package at.co.netconsulting.wakeonlan.poj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class IpAddress {
    private String ipAddressName;
    private String macAddress;
}
