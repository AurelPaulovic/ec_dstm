package com.aurelpaulovic.fiit.ec_dstm.net.message

import com.aurelpaulovic.fiit.ec_dstm.{net => net}

case class SubRemPub (pub: net.identity.Publisher) extends Message