<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['like']) && $_REQUEST['like']=='true' && $_REQUEST['userid']!='') :
  $like=1;
  $uid = $_REQUEST['uid'];
  $usid = $_REQUEST['userid'];
 $trackid = $_REQUEST['trackid'];
 $query = $conn->query("insert into likes(likes.track,likes.by) values('$trackid','$usid')");
if($query):
  $q = $conn->query("select * from tracks where tracks.id = '$trackid'");
  $val = $q->fetch_assoc();
  $uid = $val['uid'];
 $qry = $conn->query("insert into notifications(notifications.from,notifications.to,notifications.parent,notifications.child,notifications.type,notifications.read) values('$usid','$uid','$trackid','0','2','0')");
endif; 
 if($qry){
   $qry = $conn->query("select * from users where users.idu = '$uid' && users.email_like = '1'");
   if($qry){
   $row = $qry->fetch_assoc();
   $user = $row['username'];
   $email = $row['email'];
   $q = $conn->query("select * from tracks where tracks.id = '$trackid'");
   $val = $q->fetch_assoc();
   $title = $val['title'];
   $ids = $val['id'];
   $qr = $conn->query("select * from users where users.idu = '$usid'");
   $value = $qr->fetch_assoc();
   $name = $value['username'];
   
   $to = $email;
   $subject = $name.  ' liked your track';
   $message .= 'Hello  '.$user."<br>";
   $message .= '<a href="http://singering.com/index.php?a=profile&u='.$name.'">'.$name.'</a> liked your <a href="http://singering.com/index.php?a=track&id='.$ids.'&name='.urlencode($title).'">track</a>';
   $header = "From: no-reply@singering.com". "\r\n";
   $header .= "MIME-Version: 1.0". "\r\n";
   $header .= "Content-type:text/html;charset=UTF-8" . "\r\n";
   

mail($to, $subject, $message, $header);
  }
} 


 $q= $conn->query("select likes from tracks where id = '$trackid'");
 $n = $q->num_rows;
 if($n > 0) :
   $t = $q->fetch_object();
  $like =  $like + $t->likes;
   endif;
   $conn->query("update tracks set likes = '$like' where tracks.id = '$trackid'");
 if($query) :
    $set['like'][] = 'like done';
    $set['success'] =1;
 	else : 
     $set['like'][] = 'like fail';
    $set['success'] =0;
 endif;
 elseif(isset($_REQUEST['unlike']) && $_REQUEST['unlike']=='yes' && $_REQUEST['userid']!='') :
  $like=1;
 	$usid = $_REQUEST['userid'];
 $trackid = $_REQUEST['trackid'];
 $fav =1;
 $query = $conn->query("delete from likes where track='$trackid' &&  likes.by ='$usid'");
 
 if($query):
 $res = $conn->query("select * from tracks where tracks.id = '$trackid'");
  $vals = $res->fetch_assoc();
  $uids = $vals['uid'];
 $qry = $conn->query("delete from notifications where notifications.from ='$usid' && notifications.to = '$uids'");
 endif;
 
 $q= $conn->query("select likes from tracks where id = '$trackid'");
 $n = $q->num_rows;
 if($n > 0) :
   $t = $q->fetch_object();
   if($t->likes >0) {
   $like = $t->likes - $like;
   }
   else
   {
	 $like = 0;   
   }
   endif;
   $conn->query("update tracks set likes = '$like' where tracks.id = '$trackid'");
 if($query) :
    $set['like'][] = 'unlike done';
    $set['success'] =1;
 	else : 
     $set['like'][] = 'unlike fail';
    $set['success'] =0;
    endif;
else :
    $uid = $_REQUEST['usid'];
$query = $conn->query("select users.first_name,users.last_name,`tracks`.* from likes join tracks on tracks.id = likes.track join users on users.idu = tracks.uid where likes.by='$uid' && tracks.public = '1' ORDER BY likes.id DESC"); 
$num = $query->num_rows;
if($num >0) :
while($row = $query->fetch_object()) :
  $row = array('first_name'=>$row->first_name,'last_name'=>$row->last_name,'id'=>$row->id,'title'=>html_entity_decode($row->title),'description'=>$row->description,'uid'=>$row->uid,'first_name'=>$row->first_name,'last_name'=>$row->last_name,'name'=>$row->name,'tag'=>$row->tag,'art'=>$row->art,'buy'=>$row->buy,'record'=>$row->record,'release'=>$row->release,'license'=>$row->license,'size'=>$row->size,'download'=>$row->download,'time'=>$row->time,'public'=>$row->public,'likes'=>$row->likes,'downloads'=>$row->likes,'views'=>$row->views);
    $set['like'][]  = $row;
    $set['success']=1;
	endwhile;
else :
 $set['like'][] = 'No-likes';
 $set['success']=0;
endif;	
endif;
$val = json_encode($set);
echo $val;
?>