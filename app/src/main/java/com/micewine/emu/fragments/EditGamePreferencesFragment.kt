package com.micewine.emu.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.micewine.emu.R
import com.micewine.emu.activities.MainActivity.Companion.ACTION_SELECT_ICON
import com.micewine.emu.adapters.AdapterGame.Companion.selectedGameName
import com.micewine.emu.fragments.Box64PresetManagerFragment.Companion.getBox64Presets
import com.micewine.emu.fragments.ControllerPresetManagerFragment.Companion.getControllerPresets
import com.micewine.emu.fragments.ShortcutsFragment.Companion.editGameFromList
import com.micewine.emu.fragments.ShortcutsFragment.Companion.getBox64Preset
import com.micewine.emu.fragments.ShortcutsFragment.Companion.getControllerPreset
import com.micewine.emu.fragments.ShortcutsFragment.Companion.getGameExeArguments
import com.micewine.emu.fragments.ShortcutsFragment.Companion.getGameIcon
import com.micewine.emu.fragments.ShortcutsFragment.Companion.getVirtualControllerPreset
import com.micewine.emu.fragments.ShortcutsFragment.Companion.putBox64Preset
import com.micewine.emu.fragments.ShortcutsFragment.Companion.putControllerPreset
import com.micewine.emu.fragments.ShortcutsFragment.Companion.putVirtualControllerPreset
import com.micewine.emu.fragments.VirtualControllerPresetManagerFragment.Companion.getVirtualControllerPresets

class EditGamePreferencesFragment : DialogFragment() {
    private var preferences: SharedPreferences? = null
    private var imageView: ImageView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_edit_game_preferences, null)

        val editTextNewName = view.findViewById<EditText>(R.id.editTextNewName)
        val editTextArguments = view.findViewById<EditText>(R.id.appArgumentsEditText)
        val buttonContinue = view.findViewById<Button>(R.id.buttonContinue)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val selectedControllerProfileSpinner = view.findViewById<Spinner>(R.id.selectedControllerProfile)
        val selectedVirtualControllerProfileSpinner = view.findViewById<Spinner>(R.id.selectedVirtualControllerProfile)
        val selectedBox64ProfileSpinner = view.findViewById<Spinner>(R.id.selectedBox64Profile)

        imageView = view.findViewById(R.id.imageView)

        val imageBitmap = getGameIcon(selectedGameName)

        if (imageBitmap != null) {
            imageView?.setImageBitmap(
                resizeBitmap(
                    imageBitmap, imageView?.layoutParams?.width!!, imageView?.layoutParams?.height!!
                )
            )
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).setView(view).create()

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        editTextNewName.setText(selectedGameName)
        editTextArguments.setText(getGameExeArguments(selectedGameName))

        imageView?.setOnClickListener {
            requireActivity().sendBroadcast(
                Intent(ACTION_SELECT_ICON)
            )
        }

        val controllerProfilesNames: MutableList<String> = mutableListOf("--")
        val virtualControllerProfilesNames: MutableList<String> = mutableListOf("--")
        val box64ProfilesNames: MutableList<String> = mutableListOf("--")

        getControllerPresets().forEach {
            controllerProfilesNames.add(it[0])
        }

        getVirtualControllerPresets().forEach {
            virtualControllerProfilesNames.add(it.name)
        }

        getBox64Presets().forEach {
            box64ProfilesNames.add(it[0])
        }

        selectedControllerProfileSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, controllerProfilesNames)
        selectedControllerProfileSpinner.setSelection(controllerProfilesNames.indexOf(getControllerPreset(selectedGameName)))

        selectedVirtualControllerProfileSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, virtualControllerProfilesNames)
        selectedVirtualControllerProfileSpinner.setSelection(virtualControllerProfilesNames.indexOf(getVirtualControllerPreset(selectedGameName)))

        selectedBox64ProfileSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, box64ProfilesNames)
        selectedBox64ProfileSpinner.setSelection(box64ProfilesNames.indexOf(getBox64Preset(selectedGameName)))

        selectedControllerProfileSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                putControllerPreset(selectedGameName, parent?.selectedItem!!.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        selectedVirtualControllerProfileSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                putVirtualControllerPreset(selectedGameName, parent?.selectedItem!!.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        selectedBox64ProfileSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                putBox64Preset(selectedGameName, parent?.selectedItem!!.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        buttonContinue.setOnClickListener {
            val newName = editTextNewName.text.toString()
            val newArguments = editTextArguments.text.toString()

            if (newName == "") {
                return@setOnClickListener
            }

            editGameFromList(selectedGameName, newName, newArguments)

            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onResume() {
        val imageBitmap = getGameIcon(selectedGameName)

        if (imageBitmap != null) {
            imageView?.setImageBitmap(
                resizeBitmap(
                    imageBitmap, imageView?.layoutParams?.width!!, imageView?.layoutParams?.height!!
                )
            )
        }

        super.onResume()
    }

    private fun resizeBitmap(originalBitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false)
    }
}