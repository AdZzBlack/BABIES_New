<?php

defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH . '/libraries/REST_Controller.php';

/**
 * This is an example of a few basic event interaction methods you could use
 * all done with a hardcoded array
 *
 * @package         CodeIgniter
 * @subpackage      Rest Server
 * @category        Controller
 * @author          Phil Sturgeon, Chris Kacerguis
 * @license         MIT
 * @link            https://github.com/chriskacerguis/codeigniter-restserver
 */
class Master extends REST_Controller { 

    function __construct()
    {
        // Construct the parent class
        parent::__construct();

        // Configure limits on our controller methods
        // Ensure you have created the 'limits' table and enabled 'limits' within application/config/rest.php
        $this->methods['event_post']['limit'] = 500000000; // 500 requests per hour per event/key
        // $this->methods['event_delete']['limit'] = 50; // 50 requests per hour per event/key
        $this->methods['event_get']['limit'] = 500000000; // 500 requests per hour per event/key

        header("Access-Control-Allow-Origin: *");
        header("Access-Control-Allow-Methods: GET, POST");
        header("Access-Control-Allow-Headers: Origin, Content-Type, Accept, Authorization");
    }

    function ellipsis($string) {
        $cut = 30;
        $out = strlen($string) > $cut ? substr($string,0,$cut)."..." : $string;
        return $out;
    }
    
    function clean($string) {
        return preg_replace("/[^[:alnum:][:space:]]/u", '', $string); // Replaces multiple hyphens with single one.
    }

    function error($string) {
        return str_replace( array("\t", "\n") , "", $string);
    }
    
    function getGCMId($user_nomor){
        $query = "  SELECT 
                    a.gcmid
                    FROM whuser_mobile a 
                    WHERE a.status_aktif > 0 AND (a.gcmid <> '' AND a.gcmid IS NOT NULL) AND a.nomor = $user_nomor ";
        return $this->db->query($query)->row()->gcmid;
    }

    public function send_gcm($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        $this->gcm->addRecepient($registrationId);

        $this->gcm->setData(array(
            'some_key' => 'some_val'
        ));

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();

       if ($this->gcm->send())
           echo 'Success for all messages';
       else
           echo 'Some messages have errors';

       print_r($this->gcm->status);
       print_r($this->gcm->messagesStatuses);

        die(' Worked.');
    }

    public function send_gcm_group($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        foreach ($registrationId as $regisID) {
            $this->gcm->addRecepient($regisID);
        }

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();
    }
    
    function test_get()
    {
        
        $result = "a";
        
        $data['data'] = array();
        
        // START SEND NOTIFICATION
        $vcGCMId = $this->getGCMId(3);
        
        $this->send_gcm($vcGCMId, $this->ellipsis('$new_message'),'New Message(s) From ','PrivateMessage','0','0');
        
        $this->response($vcGCMId);
        
        /*
        $regisID = array();
            
        $query_getuser = " SELECT 
                            a.gcmid
                            FROM whuser_mobile a 
                            JOIN whrole_mobile b ON a.nomorrole = b.nomor
                            WHERE a.status_aktif > 0 AND (a.gcmid <> '' AND a.gcmid IS NOT NULL) AND b.approveberitaacara = 1 ";
        $result_getuser = $this->db->query($query_getuser);

        if( $result_getuser && $result_getuser->num_rows() > 0){
            foreach ($result_getuser->result_array() as $r_user){

                // START SEND NOTIFICATION
                $vcGCMId = $r_user['gcmid'];
                if( $vcGCMId != "null" ){      
                    array_push($regisID, $vcGCMId);       
                }
                
            }
            $count = $this->db->query("SELECT COUNT(1) AS elevasi_baru FROM mhberitaacara a WHERE a.status_disetujui = 0")->row()->elevasi_baru; 
            $this->send_gcm_group($regisID, $this->ellipsis("Berita Acara Elevasi"),$count . ' pending elevasi','ChooseApprovalElevasi','','');
        } 
        */
    }



    // --- POST get contact --- //
    // di union sma mhsales aja
    function getContact_post()
    {     
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "SELECT 
                    nomor AS `nomor`,
                        nama AS `nama`,
                        NULL AS `nomorhp`,
                        NULL  AS telp 
                    FROM mhadmin
                    WHERE status_aktif = 1
                    ORDER BY nama";

     //    $query = "SELECT 
     //                    nomor AS `nomor`,
     //                    nama AS `nama`,
     //                    hp AS `nomorhp`,
     //                    IFNULL(telepon, '') AS telp 
     //                    FROM mhsales

					// UNION

					// SELECT 
					//     nomor AS `nomor`,
     //                    nama AS `nama`,
     //                    hp AS `nomorhp`,
     //                    IFNULL(telepon, '') AS telp 
					// FROM mhkaryawan
					// WHERE status_aktif = 1

					// UNION
                    
     //                SELECT 
     //                nomor AS `nomor`,
     //                    nama AS `nama`,
     //                    NULL AS `nomorhp`,
     //                    NULL  AS telp 
     //                FROM mhadmin
     //                WHERE status_aktif = 1
     //                ORDER BY nama";


        
        // $query = "  SELECT 
        //                 a.nomor AS `nomor`,
        //                 a.nama AS `nama`,
        //                 a.hp AS `nomorhp`,
        //                 IFNULL(a.telepon, '') AS telp
        //             FROM mhkaryawan a
        //             WHERE a.status_aktif = 1
        //             ORDER BY a.nama;";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'nama'                  => $r['nama'],
                                                'nomorhp'               => $r['nomorhp'],
                                                'telp'                  => $r['telp']
                                                )
                );
            }
        }else{      
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }  
    
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }
    
    //v
    // --- POST get barang --- //
    function getBarang_post()
    {     

        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        
        $idjenis = (isset($jsonObject["nomorjenis"]) ? $this->clean($jsonObject["nomorjenis"])     : "");

        $query = "  SELECT 
                        a.nomor AS `nomorBarang`,
                        a.kode AS `kodeBarang`,
                        a.kodebarcode AS `kodebarcode`,
                        a.nama AS `namaBarang`,
                        c.nomor as nomorSatuan,
                        c.nama AS `satuan`
                        #a.hargabeli AS `hargabeli`,
                        #b.harga AS `hargajual`
                    FROM mhbarang a
                    #JOIN mdbarangharga b ON a.nomor = b.nomormhbarang
                    JOIN mhsatuan c ON a.nomormhsatuan = c.nomor
                    WHERE a.status_aktif = 1
                    AND a.nomormhbarangjenis = $idjenis
                    ORDER BY a.nama";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomorBarang'           => $r['nomorBarang'],
                                                'kodeBarang'            => $r['kodeBarang'],
                                                'kodebarcode'           => $r['kodebarcode'],
                                                'namaBarang'            => $r['namaBarang'], 
                                                'nomorSatuan'           => $r['nomorSatuan'],
                                                'satuan'                => $r['satuan']
                                                //'hargabeli'             => $r['hargabeli'],
                                                //'hargajual'             => $r['hargajual']
                                                )
                );
            }
        }else{      
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }  
    
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }


    //v
    // --- POST get customer --- //
    function getCustomer_post()
    {     
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        
        $query = "  SELECT 
                        a.nomor AS `nomor`,
                        a.kode AS `kode`,
                        a.nama AS `nama`,
                        a.alamat AS `alamat`,
                        a.telepon AS `telepon`
                    FROM mhrelasi a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama'], 
                                                'alamat'                => $r['alamat'],
                                                'telepon'               => $r['telepon']
                                                )
                );
            }
        }else{      
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }  
    
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    function getJenisHarga_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.nama AS `nama`
                    FROM mhjenisharga a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nomor";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'nama'                  => $r['nama']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
    
    //v
    // --- POST get valuta --- //
    function getValuta_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.kode AS `kode`,
                        a.simbol AS `simbol`
                    FROM mhvaluta a
                    WHERE a.status_aktif = 1
                    ORDER BY a.kode;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'simbol'                  => $r['simbol']
                                                //'kurs'                  => $r['kurs']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    //v
    // --- POST get kategori --- //
    function getKategori_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.nama AS `nama`,
                        a.kode AS `kode`
                    FROM mhjenisbarang a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'nama'                  => $r['nama'],
                                                'kode'                  => $r['kode']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    //v
    // --- POST get gudang --- //
    function getGudang_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.nama AS `nama`,
                        a.kode AS `kode`
                    FROM mhgudang a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama'],
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    //v
    // --- POST get jenis --- //
    function getJenis_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.kode AS `kode`,
                        a.nama AS `nama`
                    FROM mhjenisbarang a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    //v
     // --- POST get Merk --- //
    function getMerk_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.kode AS `kode`,
                        a.nama AS `nama`
                    FROM mhmerkbarang a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    // --- POST get sales --- //
    function getSales_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "SELECT DISTINCT
                    a.nomor AS `nomor`,
                    a.nama AS `nama`,
                    a.kode AS `kode`
                 #FROM whuser_mobile a
                 #JOIN tuser b
                From mhsales a
                    #ON b.nomor = a.nomortuser
                 WHERE a.status_aktif = 1
                    #AND a.nomorthsales > 0
                 ORDER BY nama";

                 //print_r($query);
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                   => $r['nomor'],
                                                //'nomortuser'          => $r['nomortuser'],
                                                //'nomorsales'            => $r['nomorsales'],
                                                'kode'                    => $r['kode'],
                                                'nama'                    => $r['nama']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    //v
    // --- POST get cabang --- //
    function getCabang_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "  SELECT
                        a.nomor AS `nomor`,
                        a.nama AS `cabang`,
                        a.kode AS `kode`
                    FROM mhcabang a
                    WHERE a.status_aktif = 1
                    ORDER BY a.nama;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomorcabang'                   => $r['nomor'],
                                                'kodecabang'                    => $r['kode'],
                                                'namacabang'                    => $r['cabang'],
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getPrice_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $idjenis = (isset($jsonObject["nomorjenis"]) ? $this->clean($jsonObject["nomorjenis"])     : "");
       // $cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "a");

        // $query = "   SELECT b.nomor AS nomor,
        //                 b.kode AS kode,
        //                 b.NamaJual AS nama,
        //                 a.HargaJual AS harga,
        //                 a.HPP AS hpp
        //             FROM tdharga a
        //             JOIN tbarang b ON a.NomorBarang = b.nomor
        //             WHERE b.Aktif = 1
        //              #AND a.nomorheader = $cabang
        //             ORDER BY b.NamaJual DESC";

        $query = "  SELECT
                         a.nomor AS `nomor`,
                         a.kode AS `kode`,
                         a.nama AS `nama`,
                         (SELECT GROUP_CONCAT(CONCAT(c.nama, '::',b.harga) SEPARATOR '@@')
                         FROM mdbarangharga b, mhjenisharga c
                         WHERE b.nomormhbarang = a.nomor 
                             AND b.status_aktif = 1 
                             AND b.harga > 0
                             AND c.nomor = b.nomormhjenisharga 
                             AND c.status_aktif = 1) AS `harga`
                        FROM mhbarang a
                        WHERE a.status_aktif = 1
                            AND a.nomormhbarangjenis = $idjenis
                        ORDER BY a.nama";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama'],
                                                'harga'                 => $r['harga']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    //--- Added by Tonny --- //
    // --- POST get PriceHPP --- //
    function getPriceHPP_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
       // $cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "a");
        $idjenis = (isset($jsonObject["nomorjenis"]) ? $this->clean($jsonObject["nomorjenis"])     : "");
        //id dari mhjenisbarang dari selection di listview

        // $query = "   SELECT b.nomor AS nomor,
        //                 b.kode AS kode,
        //                 b.NamaJual AS nama,
        //                 a.HargaJual AS harga,
        //                 a.HPP AS hpp
        //             FROM tdharga a
        //             JOIN tbarang b ON a.NomorBarang = b.nomor
        //             WHERE b.Aktif = 1
        //              #AND a.nomorheader = $cabang
        //             ORDER BY b.NamaJual DESC";

        $query = "  SELECT
                         a.nomor AS `nomor`,
                         a.kode AS `kode`,
                         a.nama AS `nama`,
                         a.hargabeli AS `hpp`,
                         (SELECT GROUP_CONCAT(CONCAT(c.nama, '::',b.harga) SEPARATOR '@@')
                         FROM mdbarangharga b, mhjenisharga c
                         WHERE b.nomormhbarang = a.nomor 
                             AND b.status_aktif = 1 
                             AND b.harga > 0
                             AND c.nomor = b.nomormhjenisharga 
                             AND c.status_aktif = 1) AS `harga`
                        FROM mhbarang a
                        WHERE a.status_aktif = 1
                        	AND a.nomormhbarangjenis = $idjenis
                        ORDER BY a.nama";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'nama'                  => $r['nama'],
                                                'harga'                 => $r['harga'],
                                                'hpp'                   => $r['hpp']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
    
    //--- Added by Shodiq ---//
    function getUsers_post()
    {
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //$query = "SELECT nomor, userid as nama FROM whuser_mobile where tipeuser = 0 order by nama desc";
        $query = "SELECT nomor, nama as nama FROM mhadmin where status_aktif <> 0 order by nama desc";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'             => $r['nomor'],
                                                'nama'              => $r['nama']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
}
