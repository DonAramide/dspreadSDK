package com.basepos.pos.ui.keyexchange

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.basepos.pos.common.domain.models.AppResponseCodes
import com.basepos.pos.common.utils.PermissionHelper
import com.basepos.pos.common.utils.showAlertDialog
import com.basepos.pos.ui.R
import com.basepos.pos.ui.databinding.ActivityKeyExchangeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KeyExchangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeyExchangeBinding
    private val viewModel: KeyExchangeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyExchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchPermissionRequest()
    }

    private fun launchPermissionRequest() {
        val permissionHelper = PermissionHelper(this)
        permissionHelper.requestPermissions(
            permissionHelper.permissions(),
            object : PermissionHelper.PermissionCallback {
                override fun onPermissionsGranted() {
                    viewModel.fetchTerminalParameters()
                    observeKeyExchangeResult()
                }
            })
    }

    private fun observeKeyExchangeResult() {
        lifecycleScope.launch {
            viewModel.keyExchangeResultState.observe(this@KeyExchangeActivity) { result ->
                when (result) {
                    is KeyExchangeScreenResult.Error -> {
                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                        ) {
                            setFinishActivity(
                                status = AppResponseCodes.FAILED.code,
                                statusMessage = getString(R.string.key_exchange_failed)
                            )
                        }
                    }

                    is KeyExchangeScreenResult.ExchangeError -> {
                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                            positiveTitle = getString(R.string.retry),
                        ) { retry ->
                            if (retry) {
                                launchPermissionRequest()
                            } else {
                                setFinishActivity(
                                    status = AppResponseCodes.FAILED.code,
                                    statusMessage = getString(R.string.key_exchange_failed)
                                )
                            }
                        }
                    }

                    is KeyExchangeScreenResult.PinPadError -> {
                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                            positiveTitle = getString(R.string.retry),
                        ) { retry ->
                            if (retry) {
                                launchPermissionRequest()
                            } else {
                                setFinishActivity(
                                    status = AppResponseCodes.FAILED.code,
                                    statusMessage = getString(R.string.key_exchange_failed)
                                )
                            }
                        }
                    }

                    is KeyExchangeScreenResult.Loading -> {}
                    is KeyExchangeScreenResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvProcessMessage.visibility = View.GONE

                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                        ) {
                            viewModel.printParameters()
                        }
                    }

                    is KeyExchangeScreenResult.PrintSuccess -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvProcessMessage.visibility = View.GONE

                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                        ) {
                            setFinishActivity(
                                status = AppResponseCodes.SUCCESS.code,
                                statusMessage = getString(R.string.key_exchange_successful)
                            )
                        }
                    }

                    is KeyExchangeScreenResult.PrintError -> {
                        showAlertDialog(
                            title = getString(R.string.key_exchange),
                            message = result.message,
                            positiveTitle = getString(R.string.retry),
                        ) { retry ->
                            if (retry) {
                                viewModel.printParameters()
                            } else {
                                setFinishActivity(
                                    status = AppResponseCodes.SUCCESS.code,
                                    statusMessage = getString(R.string.key_exchange_successful)
                                )
                            }
                        }
                    }

                    is KeyExchangeScreenResult.Printing -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvProcessMessage.visibility = View.VISIBLE

                        binding.tvProcessMessage.text = getString(R.string.printing)
                    }
                }
            }
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