/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
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

/**
 * The chat fragment
 * 
 */
public class ChatFragment extends Fragment implements ChatListener, OnClickListener {

    private ArrayList<ChatMessage> mConversation = new ArrayList<ChatMessage>();
    private EditText mChatEditText;
    private ListView mChatList;

    /**
     * Create a new instance of this fragment
     * 
     * @param pNUm
     * @return
     */
    static ChatFragment newInstance(final int pNUm) {
        final ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        // Make this fragment retain it's instance
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(final LayoutInflater pInflater, final ViewGroup pViewGroup, final Bundle pBundle) {

        // Inflate the chat view
        final View chat = pInflater.inflate(R.layout.fragment_chat, pViewGroup, false);
        // Find the chat message field
        mChatEditText = ((EditText) chat.findViewById(R.id.msg_field));
        // Set the onClickListener for the send-button
        ((ImageButton) chat.findViewById(R.id.btn_send)).setOnClickListener(this);
        // Find the chat list view
        mChatList = (ListView) chat.findViewById(R.id.list);

        // Set the list view's adapter.
        // TODO: refactor into own class
        mChatList.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return mConversation.size();
            }

            @Override
            public View getView(final int pPosition, final View pView, final ViewGroup pParent) {
                // We don't modify our parameters, we create copies
                View view = pView;
                if (view == null) {
                    // If the view hasn't been created yet, do so
                    view = pInflater.inflate(R.layout.chat_list_entry, null);
                }

                // Put content into the chat view
                final ChatMessage message = mConversation.get(pPosition);
                ((TextView) pView.findViewById(R.id.sender)).setText(message.getPlayerName());
                ((TextView) pView.findViewById(R.id.msg_text)).setText(message.getMessage());
                // TODO: Set message's timestamp

                return pView;
            }

            @Override
            public long getItemId(final int pPosition) {
                return pPosition;
            }

            @Override
            public Object getItem(int pPosition) {
                return mConversation.get(pPosition);
            }
        });
        // React to enter-events of the keyboard by sending the entered message
        // http://code.google.com/p/android/issues/detail?id=2516
        mChatEditText.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(final View pView, final int pKeyCode, final KeyEvent pEvent) {
                if (pKeyCode == KeyEvent.KEYCODE_ENTER && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(null);
                    return true;
                }
                return false;
            }
        });

        // Return this Fragment's view (yes, we are still in oCreateView())
        return chat;
    }

    /**
     * Callback method for received chat messages
     */
    @Override
    public void onChatMessageRecieved(final ChatMessage pMessage) {
        // Updatte conversations
        mConversation.add(pMessage);
        // Redraw list
        ((BaseAdapter) mChatList.getAdapter()).notifyDataSetChanged();
    }

    /**
     * onClick for sending chat messages
     */
    @Override
    public void onClick(final View pView) {
        // Create a new chatMessage
        final ChatMessage message = new ChatMessage();
        // Add the chat text to the new message
        message.setMessage(mChatEditText.getText().toString());
        // Request focus on the chat textfield
        mChatEditText.requestFocusFromTouch();
        // Reset the text in the chat textfield
        mChatEditText.setText("");
        //TODO: Add sending messages and in this method, set the players name
    }
}
