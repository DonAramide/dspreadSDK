package com.basepos.pos.ui.parameter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.basepos.pos.common.domain.models.AppResponseCodes
import com.basepos.pos.common.domain.models.ParameterResponse
import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.common.utils.UtilMethods
import com.basepos.pos.ui.R
import com.basepos.pos.ui.databinding.ActivityParameterBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ParameterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParameterBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParameterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            fetchTerminalParameters()
        }
    }

    private fun fetchTerminalParameters() {
        if (sessionManager.getHostParameters() != null) {
            val parameterResponse = ParameterResponse(
                terminalId = sessionManager.getHostParameters()?.terminalId ?: "",
                merchantId = sessionManager.getHostParameters()?.merchantId ?: "",
                merchantName = sessionManager.getHostParameters()?.merchantName ?: "",
                serialNumber = sessionManager.getTerminalSerialNo() ?: "",
                ptsp = null,
                footerMessage = "Thank you for using POS",
                bankName = sessionManager.getTmsParameter()?.bankName ?: "",
                bankLogo = sessionManager.getTmsParameter()?.bankLogo ?: "",
                baseAppVersion = UtilMethods.getPackageInfo(this)?.versionName,
                location = sessionManager.getHostParameters()?.cardAcceptorLocation ?: "",
                merchantCategoryCode = sessionManager.getHostParameters()?.mcc ?: "",
                currency = sessionManager.getHostParameters()?.currencyCode ?: ""
            )
            setFinishActivity(
                status = AppResponseCodes.SUCCESS.code,
                data = Gson().toJson(parameterResponse)
            )
        } else {
            setFinishActivity(
                status = AppResponseCodes.FAILED.code,
                statusMessage = getString(R.string.no_parameter_available_do_key_exchange)
            )
        }
    }

    private fun setFinishActivity(
        status: String,
        statusMessage: String? = null,
        data: String? = null
    ) {
        val responseIntent = Intent()
        responseIntent.putExtra("status", status)
        responseIntent.putExtra("data", data)
        responseIntent.putExtra("statusMessage", statusMessage)
        setResult(RESULT_OK, responseIntent)

        finish()
    }
}