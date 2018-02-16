<?php
session_start(); 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);

if($_REQUEST['email']) 
{
  
  $email = $_REQUEST['email'];
  $query = $db->query("select * from users where  email = '$email'");
      $num = $query->num_rows;
      if($num > 0) 
      {
      	$row = $query->fetch_object();
      	$usermail = $row->email;
      	$id=$row->idu;
      	$to = $usermail;
      	$_SESSION['requesttime']=time();
      	$_SESSION['resetid']=hash('sha256',session_id());
        $sessionid=$_SESSION['resetid'];
        $subject = "Reset Password";
        $headers .= "Content-Type: text/html; charset=ISO-8859-1\r\n";
        $headers .="<h1>Forget Password</h1>
        <hr>";
        $txt .= "<button style='background-color:#ff4d4d;color:#ffffff;border: 1px solid #ff4d4d;width:100%;height:5%;'><a href='http://singering.com/myapi/resetpassword.php?token=$id&id=$sessionid&reset=true' style='color:#ffffff;text-decoration: none;'>Reset Password</a></button>";
        if(mail($to,$subject,$txt,$headers))
        {
      	$res['email_exist'] = 'Email Sent Your Email';
      	$res['success']=1;
        }
        else 
        {
        $res['email_exist'][] = 'Request Fail';
      	$res['success']=0;

        }	
      	$val = json_encode($res); 
      	echo $val;
      }
      else 
      {
           $res['email_exist'] ='This Email Is Not Existed';
           $res['success']=0;
           $val = json_encode($res);
           echo $val;
      }

}
?>