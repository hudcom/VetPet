package com.project.vetpet.view.tabs.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.project.vetpet.databinding.ActivityEditAccountBinding
import com.project.vetpet.utils.ToastNotifier
import com.project.vetpet.utils.TypeLogs
import com.project.vetpet.view.TAG
import com.project.vetpet.view.dialog.CustomDialog
import com.project.vetpet.view.dialog.DialogListener
import com.project.vetpet.view.factory

class EditAccountActivity : AppCompatActivity(), ToastNotifier, DialogListener {

    private lateinit var binding: ActivityEditAccountBinding
    private val viewModel: EditAccountViewModel by viewModels{ factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponent()
    }

    /**
     *
     * Initiatize methods
     *
     */
    private fun initializeComponent() {
        createNotifier()
        createToolbar()
        getBundle()
        setOnClickListeners()
    }
    private fun createNotifier(){
        // Передаємо інтерфейс у ViewModel
        viewModel.toastNotifier = this
    }
    private fun getBundle(){
        val bundle = intent.getBundleExtra("user")
        if (bundle != null) {
            viewModel.convertBundle(bundle)

            with(Editable.Factory.getInstance()){
                binding.emailEditText.text = if(viewModel.getEmail() != "null") newEditable(viewModel.getEmail()) else newEditable("")
                binding.passwordEditText.text = if(viewModel.getPassword() != "null") newEditable(viewModel.getPassword()) else newEditable("")
                binding.userNameEditText.text = if(viewModel.getName() != "Ім'я користувача") newEditable(viewModel.getName()) else newEditable("")
                binding.numberEditText.text = if(viewModel.getNumber() != "null") newEditable(viewModel.getNumber()) else newEditable("")
                binding.locationEditText.text = if(viewModel.getLocation() != "null") newEditable(viewModel.getLocation()) else newEditable("")
            }
        }
    }
    private fun setOnClickListeners(){
        binding.editUserBtn.setOnClickListener { onEditBtnPressed() }
        binding.deleteUserBtn.setOnClickListener { onDeleteBtnPressed() }
        binding.passwordToggle.setOnClickListener { setOnToggleButtonClickAction(binding.passwordToggle,binding.passwordEditText) }
    }
    private fun createToolbar(){
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed() }
    }


    /**
     *
     * Click listeners
     *
     */
    private fun setOnToggleButtonClickAction(toggle: ImageButton, passwordEditText: TextInputEditText){
        toggle.setOnClickListener {
            // Перемикання видимості пароля
            val inputType = if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            passwordEditText.inputType = inputType

            // Переміщення курсора в кінець тексту
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }
    }

    private fun onEditBtnPressed(){
        startLoadingAnimation()
        if (viewModel.getEmail() != binding.emailEditText.text.toString() || viewModel.getPassword() != binding.passwordEditText.text.toString()){
            if (!viewModel.checkEmailVerification()){
                showToast("Ваш email не верифіковано ви не можете змінити email або пароль")
                resetChanges()
            }
        }
        if (viewModel.editElement(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                binding.userNameEditText.text.toString(),
                binding.numberEditText.text.toString(),
                binding.locationEditText.text.toString()
            ){ finalizeActivity() }) return
        else {
            stopLoadingAnimation()
        }
    }
    private fun onDeleteBtnPressed(){
        CustomDialog(this).createDialog(this, "Підтвердження","Ви дійсно хочете видалити користувача?")
    }

    override fun onPositiveClick() {
        startLoadingAnimation()
        viewModel.deleteUserAccount(viewModel.getEmail(),viewModel.getPassword()){
            finalizeActivity()
        }
    }

    override fun onNegativeClick() {
        writeLogs("Choose Ні")
    }

    private fun resetChanges(){
        binding.emailEditText.text =
            if(viewModel.getEmail() != "null")
                Editable.Factory.getInstance().newEditable(viewModel.getEmail())
            else
                Editable.Factory.getInstance().newEditable("")

        binding.passwordEditText.text =
            if(viewModel.getPassword() != "null")
                Editable.Factory.getInstance().newEditable(viewModel.getPassword())
            else
                Editable.Factory.getInstance().newEditable("")
    }


    /**
     *
     * Support methods
     *
     */
    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun finalizeActivity(){
        hideKeyboard(binding.editBlock)
        stopLoadingAnimation()
        this.finish()
    }


    /**
     *
     * Animation Methods
     *
     */
    private fun startLoadingAnimation() {
        binding.progressBar.bringToFront()
        binding.editBlock.alpha = 0.6f
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun stopLoadingAnimation() {
        binding.editBlock.alpha = 1f
        binding.progressBar.visibility = View.GONE
    }


    /**
     *
     * Override methods
     *
     */
    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun writeLogs(message: String, type: TypeLogs) {
        when(type){
            TypeLogs.INFO -> Log.d(TAG,message)
            TypeLogs.ERROR -> Log.e(TAG,message)
            TypeLogs.WARNING -> Log.w(TAG,message)
        }
    }


}