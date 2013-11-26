package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}

case class DSUnregisterIdentity (what: net.identity.Identity) extends Message