package com.example.safepark.ui.contactUs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.safepark.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ContactUsFragment extends Fragment {

    private Button sendEmailBtn;
    private TextInputLayout subjectTextInput, messageTextInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subjectTextInput = getView().findViewById(R.id.emailSubjectField);
        messageTextInput = getView().findViewById(R.id.emailMessageField);
        sendEmailBtn = getView().findViewById(R.id.sendEmailBtn);
        sendEmailBtn.setOnClickListener(v -> {
            String subject = Objects.requireNonNull(subjectTextInput.getEditText()).getText().toString().trim();
            String message = Objects.requireNonNull(messageTextInput.getEditText()).getText().toString().trim();
            String email = "safepark@zohomail.com";
            if (subject.isEmpty()) {
                Toast.makeText(getContext(), "Please add Subject", Toast.LENGTH_SHORT).show();
            } else if (message.isEmpty()) {
                Toast.makeText(getContext(), "Please add some Message", Toast.LENGTH_SHORT).show();
            } else {
                String mail = "mailto:" + email +
                        "?&subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(message);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(mail));
                try {
                    startActivity(Intent.createChooser(intent, "Send Email.."));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}