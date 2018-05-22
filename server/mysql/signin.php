<?php
    date_default_timezone_set('Asia/Seoul');
    $date = date('w');

    $link=mysqli_connect("localhost","admin","admin@)!*", "ohdormitory" );

    if (!$link)
    {
        echo "MySQL 접속 에러 : ";
        echo mysqli_connect_error();
        exit();
    }

    mysqli_set_charset($link,"utf8");


    $sql="select * from wash_existing_user where wash_day=".$date;

    $result=mysqli_query($link,$sql);
    $data = array();
    if($result){

        while($row=mysqli_fetch_array($result)){
            array_push($data,
                array('wash_day'=>$row[0],
                'washer_num'=>$row[1],
                'wash_time'=>$row[2],
                'using_room'=>$row[3]
            ));
        }

        $total = array();
        $total['wash_existing_user'] = $data;

    }else{
        echo "SQL문 처리중 에러 발생 : ";
        echo mysqli_error($link);
    }


    $sql="select * from wash_applying_user where wash_day=".$date;

    $result=mysqli_query($link,$sql);
    $data = array();
    if($result){

        while($row=mysqli_fetch_array($result)){

            $sub_sql = "select name, room_num from user where emirim_id='".$row[4]."'";
            
            $sub_result=mysqli_query($link,$sub_sql);
            $sub_row = mysqli_fetch_array($sub_result);
            
        
            echo $sub_row[1].'호 '.$sub_row[0];

            array_push($data, array(
                'wash_id' => $row[0],
                'wash_day'=>$row[1],
                'washer_num'=>$row[2],
                'wash_time'=>$row[3],
                'emirim_id'=>$sub_row[1].'호 '.$sub_row[0]
             ));
        }
        
        $total['wash_applying_user'] = $data;
    }else{
        echo "SQL문 처리중 에러 발생 : ";
        echo mysqli_error($link);
    }



    header('Content-Type: application/json; charset=utf8');
    $json = json_encode($total, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
    mysqli_close($link);

?>
