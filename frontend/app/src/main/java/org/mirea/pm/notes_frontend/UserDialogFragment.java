package org.mirea.pm.notes_frontend;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.mirea.pm.notes_frontend.databinding.DialogUserBinding;
import org.mirea.pm.notes_frontend.util_storage.JwtStorage;

public class UserDialogFragment extends DialogFragment {

    public static String TAG = "UserDialog";

    private final static String USERNAME_ARG_KEY = "username";

    public static UserDialogFragment newInstance(String username) {
        UserDialogFragment fragment = new UserDialogFragment();

        Bundle args = new Bundle();
        args.putString(USERNAME_ARG_KEY, username);
        fragment.setArguments(args);

        return fragment;
    }

    DialogUserBinding binding;
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogUserBinding.inflate(requireActivity().getLayoutInflater());

        Bundle args = requireArguments();
        binding.username.setText(getString(R.string.username_line_text, args.getString(USERNAME_ARG_KEY)));

        binding.dismissButton.setOnClickListener(view -> dismiss());

        binding.logoutButton.setOnClickListener(view -> {
            JwtStorage.clear(requireContext());
            Intent intent = new Intent(requireActivity(), AuthActivity.class);
            startActivity(intent);
            dismiss();
        });

        builder.setView(binding.getRoot());
        return builder.create();
    }

}
