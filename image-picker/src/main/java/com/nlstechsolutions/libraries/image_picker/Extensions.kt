package com.nlstechsolutions.libraries.image_picker

import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * ImagePicker
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 6/8/22 at 4:52 PM
 */
fun BottomSheetDialogFragment?.show(activity: FragmentActivity) {
    if ((this?.isVisible) == false) {
        this.show(activity.supportFragmentManager, activity.localClassName)
        activity.supportFragmentManager.executePendingTransactions()
    }
}