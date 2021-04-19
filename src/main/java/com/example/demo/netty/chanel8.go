//package main
//author: Lubia Yang
//create: 2013-10-21
//about: www.lubia.me

//本次更改，要增加float 的解析
package main

import (
	"encoding/binary"
	"encoding/json"
	"os"
	"strings"

	"fmt"
	"math"
	"net"
	"time"

	"github.com/go-redis/redis"
)

const (
	funcCodeReadDiscreteInputs = 2
	funcCodeReadCoils          = 1
	funcCodeWriteSingleCoil    = 5
	funcCodeWriteMultipleCoils = 15

	// 16-bit access
	funcCodeReadInputRegisters         = 4
	funcCodeReadHoldingRegisters       = 3
	funcCodeWriteSingleRegister        = 6
	funcCodeWriteMultipleRegisters     = 16
	funcCodeReadWriteMultipleRegisters = 23
	funcCodeMaskWriteRegister          = 22
	funcCodeReadFIFOQueue              = 24
)

const (
	exceptionCodeIllegalFunction                    = 1
	exceptionCodeIllegalDataAddress                 = 2
	exceptionCodeIllegalDataValue                   = 3
	exceptionCodeServerDeviceFailure                = 4
	exceptionCodeAcknowledge                        = 5
	exceptionCodeServerDeviceBusy                   = 6
	exceptionCodeMemoryParityError                  = 8
	exceptionCodeGatewayPathUnavailable             = 10
	exceptionCodeGatewayTargetDeviceFailedToRespond = 11
)

// Table of CRC values for high–order byte
var crcHighBytes = []byte{
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
	0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
}

// Table of CRC values for low-order byte
var crcLowBytes = []byte{
	0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04,
	0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09, 0x08, 0xC8,
	0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC,
	0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3, 0x11, 0xD1, 0xD0, 0x10,
	0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
	0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A, 0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38,
	0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C,
	0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26, 0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0,
	0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4,
	0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F, 0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
	0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C,
	0xB4, 0x74, 0x75, 0xB5, 0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0,
	0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54,
	0x9C, 0x5C, 0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98,
	0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
	0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40,
}

// Cyclical Redundancy Checking
type crc struct {
	high byte
	low  byte
}

func (crc *crc) reset() *crc {
	crc.high = 0xFF
	crc.low = 0xFF
	return crc
}

func (crc *crc) pushBytes(bs []byte) *crc {
	var idx, b byte

	for _, b = range bs {
		idx = crc.low ^ b
		crc.low = crc.high ^ crcHighBytes[idx]
		crc.high = crcLowBytes[idx]
	}
	return crc
}

func (crc *crc) value() uint16 {
	return uint16(crc.high)<<8 | uint16(crc.low)
}

const (
	rtuMinSize = 4
	rtuMaxSize = 256

	rtuExceptionSize = 5
)

type modbusError struct {
	functionCode  byte
	exceptionCode byte
}

// Error converts known modbus exception code to error message.
func (e *modbusError) Error() string {
	var name string
	switch e.exceptionCode {
	case exceptionCodeIllegalFunction:
		name = "illegal function"
	case exceptionCodeIllegalDataAddress:
		name = "illegal data address"
	case exceptionCodeIllegalDataValue:
		name = "illegal data value"
	case exceptionCodeServerDeviceFailure:
		name = "server device failure"
	case exceptionCodeAcknowledge:
		name = "acknowledge"
	case exceptionCodeServerDeviceBusy:
		name = "server device busy"
	case exceptionCodeMemoryParityError:
		name = "memory parity error"
	case exceptionCodeGatewayPathUnavailable:
		name = "gateway path unavailable"
	case exceptionCodeGatewayTargetDeviceFailedToRespond:
		name = "gateway target device failed to respond"
	default:
		name = "unknown"
	}
	return fmt.Sprintf("modbus: exception '%v' (%s), function '%v'", e.exceptionCode, name, e.functionCode)
}

// Packager specifies the communication layer.
type packager interface {
	encode(pdu *protocolDataUnit) (adu []byte, err error)
	decode(adu []byte) (pdu *protocolDataUnit, err error)
	verify(aduRequest []byte, aduResponse []byte) (err error)
}

// Transporter specifies the transport layer.
type transporter interface {
	senddata(aduRequest []byte) (aduResponse []byte, err error)
}

type clientHandler interface {
	packager
	transporter
}

type client struct {
	packager    packager
	transporter transporter
}

type protocolDataUnit struct {
	functionCode byte
	data         []byte
}

var transactionID uint32
var slaveID byte

func responseError(response *protocolDataUnit) error {
	mbError := &modbusError{functionCode: response.functionCode}
	if response.data != nil && len(response.data) > 0 {
		mbError.exceptionCode = response.data[0]
	}
	return mbError
}

func dataBlock(value ...uint16) []byte {
	data := make([]byte, 2*len(value))
	for i, v := range value {
		binary.BigEndian.PutUint16(data[i*2:], v)
	}
	return data
}

// dataBlockSuffix creates a sequence of uint16 data and append the suffix plus its length.
func dataBlockSuffix(suffix []byte, value ...uint16) []byte {
	length := 2 * len(value)
	data := make([]byte, length+1+len(suffix))
	for i, v := range value {
		binary.BigEndian.PutUint16(data[i*2:], v)
	}
	data[length] = uint8(len(suffix))
	copy(data[length+1:], suffix)
	return data
}

func encode(pdu *protocolDataUnit) (adu []byte, err error) {
	length := len(pdu.data) + 4
	if length > rtuMaxSize {
		err = fmt.Errorf("modbus: length of data '%v' must not be bigger than '%v'", length, rtuMaxSize)
		return
	}
	adu = make([]byte, length)

	adu[0] = slaveID
	adu[1] = pdu.functionCode
	copy(adu[2:], pdu.data)

	// Append crc
	var crc crc
	crc.reset().pushBytes(adu[0 : length-2])
	checksum := crc.value()

	adu[length-1] = byte(checksum >> 8)
	adu[length-2] = byte(checksum)
	return
}

// Verify verifies response length and slave id.
func verify(aduRequest []byte, aduResponse []byte) (err error) {
	length := len(aduResponse)
	// Minimum size (including address, function and CRC)
	if length < rtuMinSize {
		err = fmt.Errorf("modbus: response length '%v' does not meet minimum '%v'", length, rtuMinSize)
		return
	}
	// Slave address must match
	if aduResponse[0] != aduRequest[0] || aduResponse[1] != aduRequest[1] {
		err = fmt.Errorf("modbus: response slave id '%v' does not match request '%v'", aduResponse[0], aduRequest[0])
		return
	}
	return
}

// Decode extracts PDU from RTU frame and verify CRC.
func decode(adu []byte) (pdu *protocolDataUnit, err error) {
	length := len(adu)
	// Calculate checksum
	var crc crc
	crc.reset().pushBytes(adu[0 : length-2])
	checksum := uint16(adu[length-1])<<8 | uint16(adu[length-2])
	if checksum != crc.value() {
		err = fmt.Errorf("modbus: response crc '%v' does not match expected '%v'", checksum, crc.value())
		return
	}
	// Function code & data
	pdu = &protocolDataUnit{}
	pdu.functionCode = adu[1]
	pdu.data = adu[2 : length-2]
	return
}

func byteToFloat32(bytes []byte) float32 {
	bits := binary.BigEndian.Uint32(bytes)
	//bits := binary.LittleEndian.Uint32(bytes)

	return math.Float32frombits(bits)
}

var clientIndex int

var modbusDB [50]uint16

func main() {

	tcpAddr, err := net.ResolveTCPAddr("tcp4", ":10122")
	checkError(err)
	listener, err := net.ListenTCP("tcp", tcpAddr)
	checkError(err)
	clientIndex = 0

	for {
		conn, err := listener.Accept()
		if err != nil {
			fmt.Println("监听错误: ", err.Error())
			continue
		}
		clientIndex++

		go handleClient(conn, clientIndex)
	}

}

func handleClient(conn net.Conn, index int) {
	var j = 0
	var errData = 0
	//var jsonStu string
	//var jsonerr = 0
	//var q = 0

	//var n = 0

	wxclient := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "", // no password set
		DB:       10, // use default DB
	})
	pong, err := wxclient.Ping().Result()
	fmt.Println(pong, err)

	err = wxclient.HSet("QF01", "SetQF", "[0,0,0]").Err()
	if err != nil {
		panic(err)

	}

	fmt.Println("新用户连接, 来自: ", conn.RemoteAddr(), "index: ", index)
	webcamIP := conn.RemoteAddr().String()
	webcamIP = webcamIP[0:strings.LastIndex(webcamIP, ":")]

	err = wxclient.HSet("hysys", "webcamIP", webcamIP).Err()
	if err != nil {
		panic(err)

	}
	//var value = make([]byte, 64)

	//writer := bufio.NewWriter(conn)
	//reader := bufio.NewReader(conn)
	var results = make([]byte, 512)
	//var setResults = make([]byte, 12)
	//var tempbyte = make([]byte, 4)
	//	var tempfloat = make([]float32, 30)
loop:
	for {

		fmt.Println(errData)

		//IPM 2
		slaveID = 1

		request := protocolDataUnit{
			functionCode: funcCodeReadInputRegisters,
			data:         dataBlock(0, 5),
		}
		aduRequest, err := encode(&request)
		_, err = conn.Write(aduRequest)
		if err != nil {
			fmt.Println("tcp2 write error:", err)
		}
		time.Sleep(time.Second * 1)
		timeoutDuration := 5 * time.Second
		conn.SetReadDeadline(time.Now().Add(timeoutDuration))

		_, err = conn.Read(results)
		if err != nil {
			fmt.Println("tcp2 read error:", err)
			errData |= 1 << 0

		} else {

			if err := verify(aduRequest, results); err != nil {
				errData |= 1 << 0
				fmt.Println("unit2 TCP data error ", errData)

			} else {
				resData, err := decode(results[0:15])
				if err != nil {
					errData |= 1 << 0
					fmt.Println("unit2 modbus data error ", errData)
				} else {
					count := int(resData.data[0])
					length := len(resData.data) - 1
					if count != length {
						err = fmt.Errorf("modbus: response data size '%v' does not match count '%v'", length, count)

					}
					modbusData := resData.data[1:13]
					//	fmt.Println(modbusData)
					j = 0
					for i := 0; i < (len(modbusData) - 2); i += 2 {
						modbusDB[j] = uint16(modbusData[i])<<8 | uint16(modbusData[i+1])
						//modbusDB[j] = uint16(byteToFloat32(modbusData[i:i+4]) * 10)
						j++
					}
					jsonStu, jsonerr := json.Marshal(modbusDB[0:5])
					if jsonerr != nil {
						fmt.Println("生成json字符串错误")
					}

					fmt.Printf("%s\n", jsonStu)
					errData &= 0xfe

					err = wxclient.HSet("QF01", "wkq", string(jsonStu)).Err()
					if err != nil {
						panic(err)

					}

				}
			}

		}

		if (errData & 0x01) == 0x01 {
			conn.Close()
			clientIndex = 0

			fmt.Println("remove num is: ", index, "clienter")
			break loop
		}

	}

}

func checkError(err error) {
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

}
