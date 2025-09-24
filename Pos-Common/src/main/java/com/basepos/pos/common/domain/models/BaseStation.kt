package com.basepos.pos.common.domain.models

import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import androidx.annotation.Keep
import timber.log.Timber
/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

/**
 * This class would hold every info about the cell tower
 * the device is registered to
 */
@Keep
data class BaseStation(
    var mcc: Int? = null, // Mobile Country Code
    var mnc: Int? = null, // Mobile Network Code
    var lac: Int? = null, // Location Area Code or TAC(Tracking Area Code) for LTE
    var cid: Int? = null, // Cell Identity
    var arfcn: Int? = null, // Absolute RF Channel Number (or UMTS Absolute RF Channel Number for WCDMA)
    /**
     * bsic for GSM, psc for WCDMA,
     * pci for LTE, GSM has #getPsc() but always get Integer.MAX_valUE,
     * psc is undefined for GSM
     */
    var bsic_psc_pci: Int? = null,
    var lon: Double? = null, // Base station longitude
    var lat: Double? = null, // Base station latitude
    /**
     * Signal level as an asu value, asu is calculated based on 3GPP RSRP
     * for GSM, between 0..31, 99 is unknown
     * for WCDMA, between 0..31, 99 is unknown
     * for LTE, between 0..97, 99 is unknown
     * for CDMA, between 0..97, 99 is unknown
     */
    var asuLevel: Int? = null,
    var signalLevel: Int? = null, // Signal level as an int from 0..4
    var dbm: Int? = null, // Signal strength as dBm
    var type: String? = null, // Signal type, GSM or WCDMA or LTE or CDMA
) {
    companion object {
        fun bindCellInfoData(cellInfo: CellInfo): BaseStation? {
            //Base stations have different signal types：2G，3G，4G
            return when (cellInfo) {
                is CellInfoWcdma -> {
                    //China Unicom 3G
                    val cellIdentityWcdma = cellInfo.cellIdentity
                    val baseStation = BaseStation()
                    baseStation.type = "WCDMA"
                    baseStation.cid = cellIdentityWcdma.cid
                    baseStation.lac = cellIdentityWcdma.lac
                    baseStation.mcc = cellIdentityWcdma.mcc
                    baseStation.mnc = cellIdentityWcdma.mnc
                    baseStation.bsic_psc_pci = cellIdentityWcdma.psc
                    if (cellInfo.cellSignalStrength != null) {
                        baseStation.asuLevel =
                            cellInfo.cellSignalStrength.asuLevel //Get the signal level as an asu value between 0..31, 99 is unknown Asu is calculated based on 3GPP RSRP.
                        baseStation.signalLevel =
                            cellInfo.cellSignalStrength.level //Get signal level as an int from 0..4
                        baseStation.dbm = cellInfo.cellSignalStrength.dbm //Get the signal strength as dBm
                    }
                    baseStation
                }

                is CellInfoLte -> {
                    //4G
                    val cellIdentityLte = cellInfo.cellIdentity
                    val baseStation = BaseStation()
                    baseStation.type = "LTE"
                    baseStation.cid = cellIdentityLte.ci
                    baseStation.mnc = cellIdentityLte.mnc
                    baseStation.mcc = cellIdentityLte.mcc
                    baseStation.lac = cellIdentityLte.tac
                    baseStation.bsic_psc_pci = cellIdentityLte.pci
                    if (cellInfo.cellSignalStrength != null) {
                        baseStation.asuLevel = cellInfo.cellSignalStrength.asuLevel
                        baseStation.signalLevel = cellInfo.cellSignalStrength.level
                        baseStation.dbm = cellInfo.cellSignalStrength.dbm
                    }
                    baseStation
                }

                is CellInfoGsm -> {
                    //2G
                    val cellIdentityGsm = cellInfo.cellIdentity
                    val baseStation = BaseStation()
                    baseStation.type = "GSM"
                    baseStation.cid = cellIdentityGsm.cid
                    baseStation.lac = cellIdentityGsm.lac
                    baseStation.mcc = cellIdentityGsm.mcc
                    baseStation.mnc = cellIdentityGsm.mnc
                    baseStation.bsic_psc_pci = cellIdentityGsm.psc
                    if (cellInfo.cellSignalStrength != null) {
                        baseStation.asuLevel = cellInfo.cellSignalStrength.asuLevel
                        baseStation.signalLevel = cellInfo.cellSignalStrength.level
                        baseStation.dbm = cellInfo.cellSignalStrength.dbm
                    }
                    baseStation
                }

                else -> {
                    //Telecom 2/3G
                    Timber.e("CDMA CellInfo................................................")
                    null
                }
            }
        }
    }
}

