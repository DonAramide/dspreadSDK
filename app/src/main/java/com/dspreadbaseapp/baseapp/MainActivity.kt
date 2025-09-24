package com.dspreadbaseapp.baseapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.basepos.pos.ui.keyexchange.KeyExchangeActivity
import com.basepos.pos.ui.transaction.TransactionActivity
import com.basepos.pos.common.domain.enums.AccountType
import com.basepos.pos.common.domain.enums.TransactionType
import com.basepos.pos.common.domain.models.PaymentRequest
import com.basepos.pos.common.domain.models.TransactionResponse
import com.basepos.pos.common.domain.models.receipt
import com.basepos.pos.common.utils.ISOUtils
import com.basepos.pos.common.utils.UtilMethods
import com.basepos.pos.ui.printer.PrinterActivity
import com.dspreadbaseapp.baseapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTestPurchase.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestPurchaseActivity::class.java))
        }

        binding.btnPrint.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                val intent = Intent(this@MainActivity, PrinterActivity::class.java)
                intent.putExtra("requestData", Gson().toJson(receipt()))
                startActivity(intent)
            }
        }

        binding.btnKeyExchange.setOnClickListener {
            startActivity(Intent(this@MainActivity, KeyExchangeActivity::class.java))
        }

        binding.btnPurchase.setOnClickListener {
            val paymentRequest = PaymentRequest(
                transType = TransactionType.PURCHASE,
                amount = 1.00,
                print = true
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_PURCHASE)
        }

        binding.btnCardBalance.setOnClickListener {
            val paymentRequest = PaymentRequest(
                transType = TransactionType.BALANCE,
                amount = 2.00,
                stan = UtilMethods.generateRandomNo(6),
                rrn = UtilMethods.generateRandomNo(12)
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_BALANCE)
        }

        binding.btnCardReversal.setOnClickListener {
            showRrnDialog(TransactionType.REVERSAL)
        }

        binding.btnCashAdvance.setOnClickListener {
            val stan = ISOUtils.getStan()
            val paymentRequest = PaymentRequest(
                transType = TransactionType.CASHADVANCE,
                amount = 1.00,
                stan = stan,
                rrn = ISOUtils.generateRetrievalReferenceNumber(stan!!),
                print = true
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_CASH_ADVANCE)
        }

        binding.btnPurchaseWithCb.setOnClickListener {
            val stan = ISOUtils.getStan()
            val paymentRequest = PaymentRequest(
                transType = TransactionType.PURCHASEWITHCB,
                amount = 1.00,
                cashbackAmount = 1.00,
                stan = stan,
                rrn = ISOUtils.generateRetrievalReferenceNumber(stan!!),
                print = true
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_PURCHASE_WITH_CB)
        }

        binding.btnRefund.setOnClickListener {
            showRrnDialog(TransactionType.REFUND)
        }

        binding.btnPreAuth.setOnClickListener {
            val stan = ISOUtils.getStan()
            val paymentRequest = PaymentRequest(
                transType = TransactionType.PREAUTH,
                amount = 1.00,
                stan = stan,
                rrn = ISOUtils.generateRetrievalReferenceNumber(stan!!),
                print = true
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_PURCHASE_WITH_CB)
        }

        binding.btnPreAuthCompletion.setOnClickListener {
            showRrnDialog(TransactionType.PREAUTHCOMPLETE)
        }
    }

    private fun showRrnDialog(transactionType: TransactionType) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter RRN")

        val editText = EditText(this)
        editText.hint = "Enter RRN"
        builder.setView(editText)

        builder.setPositiveButton("OK") { dialog, _ ->
            val rrn = editText.text.toString().trim { it <= ' ' }
            if (rrn.length < 12) {
                Toast.makeText(this@MainActivity, "RRN should be 12 digits", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                return@setPositiveButton
            }

            val paymentRequest = PaymentRequest(
                transType = transactionType,
                amount = 1.00,
                stan = ISOUtils.getStan(),
                rrn = rrn
            )

            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            intent.putExtra("requestData", Gson().toJson(paymentRequest))
            startActivityForResult(intent, RC_PURCHASE_WITH_CB)
        }

        // Set negative button and its click listener (optional)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == RC_PURCHASE) {
                val status = data?.getStringExtra("status")
                val transactionJson = data?.getStringExtra("data")
                if (transactionJson != null) {
                    val transaction =
                        Gson().fromJson(transactionJson, TransactionResponse::class.java)
                }
                return
            }

            if (requestCode == RC_BALANCE) {
                val status = data?.getStringExtra("status")
                val transactionJson = data?.getStringExtra("data")
                if (transactionJson != null) {
                    val transaction =
                        Gson().fromJson(transactionJson, TransactionResponse::class.java)
                }
            }
        }
    }

    companion object {
        const val RC_PURCHASE = 1000
        const val RC_BALANCE = 2000
        const val RC_REVERSAL = 3000
        const val RC_CASH_ADVANCE = 5000
        const val RC_PURCHASE_WITH_CB = 6000
    }
}