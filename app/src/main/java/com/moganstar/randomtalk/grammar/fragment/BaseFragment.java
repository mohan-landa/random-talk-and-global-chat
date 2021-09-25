package com.moganstar.randomtalk.grammar.fragment;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.moganstar.randomtalk.grammar.utils.CommunicatorFragmentInterface;

public abstract class BaseFragment extends Fragment {
   public CommunicatorFragmentInterface myCommunicator;
   private boolean openMenuOnBackPress = false;

   public void onAttach(Activity var1) {
      super.onAttach(var1);

      try {
         this.myCommunicator = (CommunicatorFragmentInterface)var1;
      } catch (ClassCastException var3) {
         StringBuilder var2 = new StringBuilder();
         var2.append(var1.toString());
         var2.append(" must implement OnFragmentInteractionListener");
         throw new ClassCastException(var2.toString());
      }
   }

   public void onDetach() {
      super.onDetach();
      this.myCommunicator = null;
   }

   public boolean openMenuOnBackPress() {
      return this.openMenuOnBackPress;
   }

   public void setOpenMenuOnBackPress(boolean var1) {
      this.openMenuOnBackPress = var1;
   }
}
