package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}

case class SubAddPub(pub: net.identity.Publisher) extends Message