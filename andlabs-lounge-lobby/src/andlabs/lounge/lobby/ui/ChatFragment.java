/*
 * Copyright (C) 2012, 2013 ANDLABS GmbH. All rights reserved.
 *
 * www.lounge.andlabs.com
 * lounge@andlabs.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package andlabs.lounge.lobby.ui;

import java.util.ArrayList;

import andlabs.lounge.lobby.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ChatFragment extends Fragment implements ChatListener, OnClickListener {

    private ArrayList<ChatMessage> mConversation = new ArrayList<ChatMessage>();
    private EditText mChatEditText;


    static ChatFragment newInstance(int num) {
        ChatFragment f = new ChatFragment();
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onStart() {
        // ((LoungeActivity) getActivity()).getLounge().register(this);
        super.onStart();
    }


    @Override
    public View onCreateView(final LayoutInflater infl, ViewGroup p, Bundle b) {
        final View chat = infl.inflate(R.layout.fragment_chat, p, false);
        mChatEditText = ((EditText) chat.findViewById(R.id.msg_field));
        ((ImageButton) chat.findViewById(R.id.btn_send)).setOnClickListener(this);
        ((ListView) chat.findViewById(R.id.list)).setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return mConversation.size();
            }


            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = infl.inflate(R.layout.chat_list_entry, null);
                final ChatMessage msg = mConversation.get(position);
                ((TextView) view.findViewById(R.id.sender)).setText(msg.getPlayerName());
                ((TextView) view.findViewById(R.id.msg_text)).setText(msg.getMessage());
                return view;
            }


            @Override
            public long getItemId(int position) {
                return 0;
            }


            @Override
            public Object getItem(int position) {
                return null;
            }
        });
        // http://code.google.com/p/android/issues/detail?id=2516
        mChatEditText.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(null);
                    return true;
                }
                return false;
            }
        });
        return chat;
    }


    @Override
    public void onChatMessageRecieved(ChatMessage msg) {
        mConversation.add(msg);
        ((BaseAdapter) ((ListView) getView().findViewById(R.id.list)).getAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        ChatMessage msg = new ChatMessage();
        msg.setMessage(mChatEditText.getText().toString());
        mChatEditText.requestFocusFromTouch();
        mChatEditText.setText("");
        onChatMessageRecieved(msg);
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
