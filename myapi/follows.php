<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['followerfollowing']) && $_REQUEST['followerfollowing']=='true' && $_REQUEST['uid']!='') :
$uid = $_REQUEST['uid'];
$requestid = $_REQUEST['requestid'];
$follows=1;
$query = $conn->query("insert into relations(leader,subscriber) values('$requestid','$uid') ");
if($query):
$qrys = $conn->query("insert into notifications(notifications.from,notifications.to,notifications.type,notifications.read) values('$uid','$requestid','4','0') ");
endif;
 if($qrys):
$qry = $conn->query("select * from users where users.idu = '$requestid' && users.email_new_friend = '1'");
   if($qrys):
   $row = $qry->fetch_assoc();
   $user = $row['username'];
   $email = $row['email'];
   $q = $conn->query("select * from users where users.idu = '$uid'");
   $val = $q->fetch_assoc();
   $username = $val['username'];
   
   $to = $email;
   $subject = $username.  ' added you as friend';
   $message .= 'Hello  '.$user."<br>";
   $message .= '<a href="http://singering.com/index.php?a=profile&u='.$username.'">'.$username.'</a> added you as friend';
   $header = "From: no-reply@singering.com". "\r\n";
   $header .= "MIME-Version: 1.0". "\r\n";
   $header .= "Content-type:text/html;charset=UTF-8" . "\r\n";
   
mail($to, $subject, $message, $header);
   endif;
endif; 


if($query) :
 $set['request'][]='request successfull';
$set['success']=1;
else :
 $set['request'][]='request not done';
$set['success']=0;

endif;
elseif(isset($_REQUEST['followers']) && $_REQUEST['followers']=='true' && $_REQUEST['followerid']!=null) :
$followerid = $_REQUEST['followerid'];
$query = $conn->query("select users.idu,users.username,users.first_name,last_name,users.country,users.city,users.image from relations join users on users.idu = relations.subscriber   where leader = '$followerid' ORDER BY relations.id DESC");
$num = $query->num_rows;
if($num >0) :
    while($row = $query->fetch_assoc()) :
      $set['follower'][]=$row;
      $set['success'] = 1;
  endwhile;
else :
   $set['follower'][] = 'No-follower';
  $set['success']=0;
endif;
elseif($_REQUEST['unfollow']=='yes' && $_REQUEST['uid']!='') :
$uid = $_REQUEST['uid'];
$request = $_REQUEST['requestid'];
$query = $conn->query("delete from relations where leader = $request && subscriber = $uid");
if($query):
$qry = $conn->query("delete from notifications where notifications.from ='$uid' && notifications.to = '$request'");
endif;
if($query) :
   $set['unfollow'][] ='Unfollow success';
   $set['success'] =1;
else :
     $set['unfollow'][] ='Unfollow fail';
    $set['success'] =0;
endif;
 else:
$followingid = $_REQUEST['followingid'];
$requestid = $_REQUEST['requestid'];
$l = $conn->query("select  count(likes.track) as likes from tracks join likes on likes.track=tracks.id where tracks.uid = '$requestid'");
$t = $conn->query("select count(tracks.uid) as tracks   from tracks where tracks.uid = '$requestid'");
$f = $conn->query("select count('relations.leader') as follower   from relations where leader = '$requestid'");
$query = $conn->query("select users.idu,users.username,users.first_name,last_name,users.country,users.city,users.image from relations join users on users.idu = relations.leader   where subscriber = '$followingid' ORDER BY relations.id DESC");
$num = $query->num_rows;
if($num >0) :
    while($row = $query->fetch_assoc()) :
            $fl =  $f->fetch_assoc();
            $lk =  $l->fetch_assoc();
            $tr =  $t->fetch_assoc();
      $set['following'][]=$row;
      $set['success'] = 1;
  endwhile;
else :
   $set['following'][] = 'No-follower';
  $set['success']=0;
endif;


endif;
$val = json_encode($set);
echo $val;
?>