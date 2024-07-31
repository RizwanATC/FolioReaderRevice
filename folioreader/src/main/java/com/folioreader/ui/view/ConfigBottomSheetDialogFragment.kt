package com.folioreader.ui.view

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.folioreader.Config
import com.folioreader.Constants
import com.folioreader.R
import com.folioreader.model.event.ReloadDataEvent
import com.folioreader.ui.activity.FolioActivity
import com.folioreader.ui.activity.FolioActivityCallback
import com.folioreader.ui.fragment.MediaControllerFragment
import com.folioreader.util.AppUtil
import com.folioreader.util.UiUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.greenrobot.eventbus.EventBus
import androidx.appcompat.app.AlertDialog

class ConfigBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val FADE_DAY_NIGHT_MODE = 500
        @JvmField
        val LOG_TAG: String = ConfigBottomSheetDialogFragment::class.java.simpleName
    }
    private var mRootView: View? = null
    private lateinit var config: Config
    private var isNightMode = false
    private lateinit var activityCallback: FolioActivityCallback
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.view_config, container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is FolioActivity)
            activityCallback = activity as FolioActivity

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        config = AppUtil.getSavedConfig(activity)!!
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(null)
    }

    private fun initViews() {
        inflateView()
        configFonts()
        view?.findViewById<SeekBar>(R.id.view_config_font_size_seek_bar)?.progress = config.fontSize
        configSeekBar()
        selectFont(config.font, false)
        isNightMode = config.isNightMode
        val container = view?.findViewById<View>(R.id.container)
        container?.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isNightMode) R.color.night else R.color.white))

        val dayModeButton = view?.findViewById<View>(R.id.view_config_ib_day_mode)
        val nightModeButton = view?.findViewById<View>(R.id.view_config_ib_night_mode)

        if (isNightMode) {
            dayModeButton?.isSelected = false
            nightModeButton?.isSelected = true
            UiUtil.setColorIntToDrawable(config.themeColor, nightModeButton?.background)
            UiUtil.setColorResToDrawable(R.color.app_gray, dayModeButton?.background)
        } else {
            dayModeButton?.isSelected = true
            nightModeButton?.isSelected = false
            UiUtil.setColorIntToDrawable(config.themeColor, dayModeButton?.background)
            UiUtil.setColorResToDrawable(R.color.app_gray, nightModeButton?.background)
        }

        dayModeButton?.setOnClickListener {
            isNightMode = true
            toggleBlackTheme()
            dayModeButton.isSelected = true
            nightModeButton?.isSelected = false
            setToolBarColor()
            setAudioPlayerBackground()
            UiUtil.setColorResToDrawable(R.color.app_gray, nightModeButton?.background)
            UiUtil.setColorIntToDrawable(config.themeColor, dayModeButton.background)
        }

        nightModeButton?.setOnClickListener {
            isNightMode = false
            toggleBlackTheme()
            dayModeButton?.isSelected = false
            nightModeButton.isSelected = true
            UiUtil.setColorResToDrawable(R.color.app_gray, dayModeButton?.background)
            UiUtil.setColorIntToDrawable(config.themeColor, nightModeButton.background)
            setToolBarColor()
            setAudioPlayerBackground()
        }
    }

    private fun inflateView() {
        // Your existing code for inflating views
    }

    private fun configFonts() {
        val colorStateList = UiUtil.getColorList(
            config.themeColor,
            ContextCompat.getColor(requireContext(), R.color.grey_color)
        )

       /* view?.findViewById<TextView>(R.id.view_config_font_andada)?.setTextColor(colorStateList)
        view?.findViewById<TextView>(R.id.view_config_font_lato)?.setTextColor(colorStateList)
        view?.findViewById<TextView>(R.id.view_config_font_lora)?.setTextColor(colorStateList)
        view?.findViewById<TextView>(R.id.view_config_font_raleway)?.setTextColor(colorStateList)*/

        view?.findViewById<TextView>(R.id.view_config_font_andada)?.setOnClickListener { selectFont(Constants.FONT_ANDADA, true) }
        view?.findViewById<TextView>(R.id.view_config_font_lato)?.setOnClickListener { selectFont(Constants.FONT_LATO, true) }
        view?.findViewById<TextView>(R.id.view_config_font_lora)?.setOnClickListener { selectFont(Constants.FONT_LORA, true) }
        view?.findViewById<TextView>(R.id.view_config_font_raleway)?.setOnClickListener { selectFont(Constants.FONT_RALEWAY, true) }
    }

    private fun selectFont(selectedFont: Int, isReloadNeeded: Boolean) {
        // Your existing code for selecting fonts
    }

    private fun setSelectedFont(andada: Boolean, lato: Boolean, lora: Boolean, raleway: Boolean) {
        // Your existing code for setting selected font
    }

    private fun toggleBlackTheme() {
        // Your existing code for toggling the black theme
    }

    private fun updateTextColors() {
        val textColor = if (isNightMode) {
            ContextCompat.getColor(requireContext(), R.color.white)
        } else {
            ContextCompat.getColor(requireContext(), R.color.black)
        }

        /*view?.findViewById<TextView>(R.id.view_config_font_andada)?.setTextColor(textColor)
        view?.findViewById<TextView>(R.id.view_config_font_lato)?.setTextColor(textColor)
        view?.findViewById<TextView>(R.id.view_config_font_lora)?.setTextColor(textColor)
        view?.findViewById<TextView>(R.id.view_config_font_raleway)?.setTextColor(textColor)
        view?.findViewById<TextView>(R.id.view_config_tv_vertical)?.setTextColor(textColor)
        view?.findViewById<TextView>(R.id.view_config_tv_horizontal)?.setTextColor(textColor)*/
    }

    private fun configSeekBar() {
        val thumbDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.seekbar_thumb)
        UiUtil.setColorIntToDrawable(config.themeColor, thumbDrawable)
        UiUtil.setColorResToDrawable(R.color.grey_color, view?.findViewById<SeekBar>(R.id.view_config_font_size_seek_bar)?.progressDrawable)
        view?.findViewById<SeekBar>(R.id.view_config_font_size_seek_bar)?.thumb = thumbDrawable

        view?.findViewById<SeekBar>(R.id.view_config_font_size_seek_bar)?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                config.fontSize = progress
                AppUtil.saveConfig(activity, config)
                EventBus.getDefault().post(ReloadDataEvent())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setToolBarColor() {
        // Your existing code for setting toolbar color
    }

    private fun setAudioPlayerBackground() {
        // Your existing code for setting audio player background
    }
}
