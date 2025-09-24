package com.basepos.pos.common.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */

@Keep
@Parcelize
data class Receipt(
    val receipt: List<ReceiptFields>
): Parcelable

@Keep
@Parcelize
data class ReceiptFields(
    val logoPath: String,
    val stringFields: List<StringFields>
): Parcelable

@Keep
@Parcelize
data class StringFields(
    val isMultiline: Boolean = false,
    val header: TextField,
    val body: TextField? = null,
): Parcelable

@Keep
@Parcelize
data class TextField(
    val text: String,
    val align: FieldAlign? = FieldAlign.left,
    val size: FieldSize? = FieldSize.normal,
    val isBold: Boolean? = false
): Parcelable

@Keep
enum class FieldAlign {
    left,
    center,
    right
}

@Keep
enum class FieldSize {
    normal,
    small,
    large
}

fun line(): StringFields {
    return StringFields(
        isMultiline = true,
        header = TextField(text = line, isBold = true)
    )
}

suspend fun receipt(): Receipt = withContext(Dispatchers.IO) {
    return@withContext Receipt(
        receipt = listOf(
            ReceiptFields(
                logoPath = "https://anpimagehosting.s3.eu-west-1.amazonaws.com/20568_lapo_logo.png",
                stringFields = listOf(
                    StringFields(
                        isMultiline = true,
                        header = TextField(
                            text = "***** CUSTOMER COPY *****",
                            isBold = true
                        ),
                    ),
                    StringFields(
                        isMultiline = true,
                        header = TextField(
                            text = "Customer Name",
                            isBold = true
                        )
                    ),
                    StringFields(
                        isMultiline = false,
                        header = TextField(
                            text = "Terminal ID",
                        ),
                        body = TextField(
                            text = "2070AL32",
                        )
                    ),
                    StringFields(
                        isMultiline = true,
                        header = TextField(
                            text = "Thank you for using GA POS",
                        ),
                        body = TextField(
                            text = "",
                        )
                    )
                )
            )
        ),
    )
}

const val line = "--------------------------------"
