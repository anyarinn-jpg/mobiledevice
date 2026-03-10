const express = require("express");
const mysql = require("mysql");
require("dotenv").config();
const app = express();

const bcrypt = require('bcryptjs');
const saltRounds = 10;

// Middleware
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));

// MySQL Pool
const db = mysql.createPool({
  connectionLimit: 10,
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

// Promise wrapper
function query(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.query(sql, params, (err, results) => {
      if (err) return reject(err);
      resolve(results);
    });
  });
}

// Root
app.get("/", (req, res) => {
  res.json({ message: "Simple User User  (Insert & Query)" });
});

// QUERY DATA (GET)
app.get("/allUser", async (req, res) => {
  try {
    const results = await query("SELECT * FROM user");
    res.json(results);
  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

app.post("/addRoom", async (req, res) => {
  try {
    const { room_number, status, building_id, room_type_id } = req.body;

    if (!room_number || !status || !building_id || !room_type_id) {
      return res.status(400).json({ message: "Missing Data" });
    }

    const sql = `
      INSERT INTO room (room_number, status, building_id, room_type_id)
      VALUES (?, ?, ?, ?)
    `;

    await db.query(sql, [room_number, status, building_id, room_type_id]);

    res.json({ success: true });

  } catch (error) {
    console.error(error);
    res.status(500).json({ message: "Server Error" });
  }
});



// INSERT DATA (POST)
app.post("/insertUser", async (req, res) => {
  try {
    const user = req.body;

    if (!user.username || !user.password) {
      return res.status(400).json({
        error: true,
        message: "Username and password required",
      });
    }

   
    const hashedPassword = await bcrypt.hash(user.password, saltRounds);

    const newUser = {
      ...user,
      password: hashedPassword,
    };

    const result = await query("INSERT INTO user SET ?", [newUser]);

    res.status(201).json({
      error: false,
      message: "User created",
      insertId: result.insertId,
    });

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});
// PUT: Update user data by ID
app.put("/updateUser/:id", async (req, res) => {
  try {
    const user_id = req.params.id
    const { username, password } = req.body

    let updateData = {}

    if (username) updateData.username = username

    if (password) {
      const hash = await bcrypt.hash(password, 10)
      updateData.password = hash
    }

    const result = await query(
      "UPDATE user SET ? WHERE user_id = ?",
      [updateData, user_id]
    )

    res.json({ error: false, message: "Updated" })

  } catch (err) {
    res.status(500).json({ error: true, message: err.message })
  }
})

// DELETE: Remove a user by ID
app.delete('/deleteUser/:id', async (req, res) => {
  try {
    const user_id = req.params.id;

    // Validate ID
    if (!user_id) {
      return res
        .status(400)
        .json({ error: true, message: "Please provide user id" });
    }

    // Execute delete query
    const result = await query("DELETE FROM user WHERE user_id = ?", [
      user_id,
    ]);

    // Check if the record existed
    if (result.affectedRows === 0) {
      return res
        .status(404)
        .json({ error: true, message: "User ID not found" });
    }

    res.json({ error: false, message: "User has been deleted successfully" });
  } catch (err) {
    console.error("Error occurred:", err);
    res.status(500).json({ error: true, message: err.message });
  }
});


/// LOGIN: Verify credentials
app.post("/login", async (req, res) => {
  try {
    const { username, password } = req.body;

    const users = await query(
      `SELECT user_id, username, password, role, owner_id
       FROM user
       WHERE username = ?`,
      [username]
    );

    if (users.length === 0) {
      return res.status(401).json({
        error: true,
        message: "User not found",
      });
    }

    const user = users[0];

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(401).json({
        error: true,
        message: "Invalid password",
      });
    }
    console.log("LOGIN USER:", user);

    res.json({
      error: false,
      message: "Login successful",
      user_id: user.user_id,
      username: user.username,
      role: user.role,
      owner_id: user.owner_id
    });

  } catch (err) {
    res.status(500).json({
      error: true,
      message: "Internal Server Error",
    });
  }
});

//PROFILE: Display owner information by ID
app.get("/search/:id", async (req, res) => {
  try {
    const user_id = req.params.id;

    // Fetch data excluding the password field
    const results = await query(
      "SELECT user_id, user_name, role FROM user WHERE user_id = ?",
      [user_id]
    );

    if (results.length === 0) {
      return res.status(404).json({
        error: true, message: "User Record not found",
        user_id: null, user_name: null,
        role: null
      });
    }

    const user = results[0];
    res.json({
      error: false, message: "Success",
      user_id: user.user_id, user_name: user.user_name,
      role: user.role
    });

  } catch (err) {
    console.error("Profile Error:", err);
    res.status(500).json({
      error: true, message: "Internal Server Error",
      user_id: null, user_name: null,
     role: null
    });
  }
});

app.get("/dashboard", async (req, res) => {

  const totalRooms = await query("SELECT COUNT(*) total FROM room");

  const availableRooms = await query(
    "SELECT COUNT(*) total FROM room WHERE status = 'ว่าง'"
  );

  const income = await query(`
    SELECT COALESCE(SUM(total_amount), 0) AS total
    FROM utility_bill
    WHERE bill_month = MONTH(CURRENT_DATE())
    AND bill_year = YEAR(CURRENT_DATE())
    AND status IN ('paid', 'ชำระแล้ว')
  `);

  const recent = await query(`
    SELECT 
      ub.bill_id,
      ub.room_id,
      r.room_number,
      ub.water_unit,
      ub.electric_unit,
      ub.total_amount AS amount,
      COALESCE(rt.price, 0) AS room_price,
      ub.status,
      ub.bill_month,
      ub.bill_year
    FROM utility_bill ub
    JOIN room r ON ub.room_id = r.room_id
    LEFT JOIN room_type rt ON r.room_type_id = rt.room_type_id
    ORDER BY ub.bill_id DESC
    LIMIT 10
  `);

  res.json({
    summary: {
      adminName: "Admin",
      totalRooms: totalRooms[0].total,
      availableRooms: availableRooms[0].total,
      monthlyIncome: income[0].total || 0
    },
    recentPayments: recent.map(r => ({
       billId: r.bill_id,
        roomId: r.room_id,          // ✅ เพิ่ม
        roomNumber: r.room_number,
        waterUnit: r.water_unit,    // ✅ เพิ่ม
        electricUnit: r.electric_unit, // ✅ เพิ่ม
        amount: r.amount,           // ค่าน้ำ+ค่าไฟ (เดิม)
        roomPrice: r.room_price,    // ✅ เพิ่ม ค่าห้อง
        status: r.status === "paid" ? "ชำระแล้ว"
              : r.status === "pending" ? "รอตรวจสอบ"
              : r.status,
        billMonth: r.bill_month,
        billYear: r.bill_year,
        timeText: "ล่าสุด"
    }))
  });
});

// ✅ 1. ดึงบิลล่าสุดตาม room_id
app.get("/bill/:room_id", async (req, res) => {
    try {
        const room_id = req.params.room_id;

        const results = await query(
            `SELECT * FROM utility_bill 
             WHERE room_id = ?
             ORDER BY bill_year DESC, bill_month DESC
             LIMIT 1`,
            [room_id]
        );

        if (results.length === 0) {
            return res.json({ message: "No bill found" });
        }

        res.json(results[0]);

    } catch (err) {
        res.status(500).json({ error: true, message: err.message });
    }
});


// ✅ 2. เพิ่มบิลใหม่
app.post("/bill", async (req, res) => {
    try {
        const bill = req.body;

        const result = await query(
            "INSERT INTO utility_bill SET ?",
            [bill]
        );

        res.status(201).json({
            message: "Bill created",
            insertId: result.insertId
        });

    } catch (err) {
        res.status(500).json({ error: true, message: err.message });
    }
});


// ✅ 3. อัพเดทสถานะการชำระเงิน
app.put("/bill/status", async (req, res) => {
    try {
        const { bill_id, status } = req.body;

        const result = await query(
            "UPDATE utility_bill SET status=? WHERE bill_id=?",
            [status, bill_id]
        );

        res.json({
            message: "Status updated",
            affectedRows: result.affectedRows
        });

    } catch (err) {
        res.status(500).json({ error: true, message: err.message });
    }
});

app.get("/allRooms", async (req, res) => {
    try {
        const month = req.query.month;
        const year = req.query.year;
        const currentMonth = month || String(new Date().getMonth() + 1).padStart(2, '0');
        const currentYear = year || String(new Date().getFullYear());

        const sql = `
            SELECT 
                r.room_id, r.room_number, r.building_id, r.status, 
                r.previous_elec, r.current_elec, r.previous_water, r.current_water,
                r.owner_id, 
                MAX(t.user_id) AS user_id, 
                MAX(t.first_name) AS first_name, 
                MAX(t.last_name) AS last_name, 
                ub.total_amount AS last_bill_amount,
                ub.status AS bill_status, ub.bill_id, ub.bill_month, ub.bill_year
            FROM room r
            LEFT JOIN tenant t ON r.room_id = t.room_id
            LEFT JOIN (
                SELECT * FROM utility_bill 
                WHERE bill_id IN (
                    SELECT MAX(bill_id) 
                    FROM utility_bill 
                    WHERE bill_month = ? AND bill_year = ?
                    GROUP BY room_id
                )
            ) ub ON r.room_id = ub.room_id
            GROUP BY r.room_id
        `;
        const results = await query(sql, [currentMonth, currentYear]);
        res.json(results);
    } catch (err) {
        console.error("SQL Error:", err.message);
        res.status(500).json({ error: err.message });
    }
});

// --- แก้ไข API POST /addBill ---
app.post("/addBill", async (req, res) => {
    // 1. รับ user_id เพิ่มมาจากที่แอป Android ส่งมา
    const { room_id, user_id, bill_month, bill_year, water_unit, electric_unit, total_amount, status } = req.body;

    try {
        // 2. ดึง owner_id จากตาราง room (เพราะ room_id ผูกกับ owner_id อยู่แล้ว)
        const sqlGetOwner = `SELECT owner_id FROM room WHERE room_id = ? LIMIT 1`;
        const roomResult = await query(sqlGetOwner, [room_id]);

        if (roomResult.length === 0) {
            return res.status(404).json({ success: false, message: "ไม่พบข้อมูลห้อง" });
        }

        const owner_id = roomResult[0].owner_id;

        // 3. บันทึกลง utility_bill (ใส่ user_id และ owner_id ที่ได้มา)
        const sqlInsertBill = `INSERT INTO utility_bill 
            (room_id, user_id, owner_id, bill_month, bill_year, water_unit, electric_unit, total_amount, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;
        
        await query(sqlInsertBill, [
            room_id, 
            user_id,    // ใช้ user_id ที่ส่งมาจาก Android
            owner_id, 
            bill_month, 
            bill_year, 
            water_unit, 
            electric_unit, 
            total_amount, 
            status
        ]);

        // 4. อัปเดตมิเตอร์ในตาราง room
        const sqlUpdateRoom = `UPDATE room SET current_elec = ?, current_water = ? WHERE room_id = ?`;
        await query(sqlUpdateRoom, [electric_unit, water_unit, room_id]);
        
        res.json({ success: true, message: "บันทึกบิลสำเร็จ" });

    } catch (err) {
        console.error("Server Error:", err);
        res.status(500).json({ error: err.message });
    }
});

app.get("/allAvailableRoom", async (req, res) => {
try {
const results = await query("SELECT * FROM room WHERE status = 'ว่าง'");
res.json(results);
} catch (err) {
res.status(500).json({ error: true, message: err.message });

}
});

app.get("/allUnavailableRoom", async (req, res) => {
try {
const results = await query("SELECT * FROM room WHERE status = 'ไม่ว่าง'");
res.json(results);
} catch (err) {
res.status(500).json({ error: true, message: err.message });

}
});


app.post("/insertRoom", async (req, res) => {
    try {
        // เพิ่ม owner_id ในการรับค่าจาก req.body
        const { room_number, status, building_id, room_type_id, owner_id, picture } = req.body;

        const result = await query(
            `INSERT INTO room 
            (room_number, status, building_id, room_type_id, owner_id, picture)
            VALUES (?, ?, ?, ?, ?, ?)`, // เพิ่มฟิลด์ owner_id ใน SQL
            [
                room_number,
                status,
                building_id,
                room_type_id,
                owner_id, // ส่งค่า ID เข้าไปบันทึก
                picture || null
            ]
        );

        res.json({
            error: false,
            room_id: result.insertId
        });

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: true, message: err.message });
    }
});

// PUT: Update room data by ID
app.put("/updateRoom/:id", async (req, res) => {
  try {
    const room_id = req.params.id;
    const room = req.body;

    // Validate ID and data
    if (!room_id || !room || Object.keys(room).length === 0) {
      return res.status(400).json({
        error: true,
        message: "Please provide room id and room data",
      });
    }

    // Execute update query
    const result = await query("UPDATE room SET ? WHERE room_id = ?", [
      room,
      room_id,
    ]);

    // Check if the record was actually found and updated
    if (result.affectedRows === 0) {
      return res
        .status(404)
        .json({ error: true, message: "Room ID not found" });
    }

    res.json({ error: false, message: "Room has been updated successfully" });
  } catch (err) {
    console.error("Error occurred:", err);
    res.status(500).json({ error: true, message: err.message });
  }
});

// 🔹 แก้ไขห้อง
app.put("/room/:id", async (req, res) => {
  try {
    const room_id = req.params.id;
    const room = req.body;

    if (!room_id || !room || Object.keys(room).length === 0) {
      return res.status(400).json({
        error: true,
        message: "Room ID and data required"
      });
    }

    const result = await query(
      "UPDATE room SET ? WHERE room_id = ?",
      [room, room_id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({
        error: true,
        message: "Room not found"
      });
    }

    res.json({
      error: false,
      message: "Room updated successfully"
    });

  } catch (err) {
    console.error("UPDATE ROOM ERROR:", err);
    res.status(500).json({
      error: true,
      message: err.message
    });
  }
});

app.put("/room/status/:id", async (req, res) => {
    try {
        const roomId = req.params.id;
        const { status } = req.body;

        // ถ้าพยายามจะตั้งค่าเป็น 'ไม่ว่าง'
        if (status === "ไม่ว่าง") {
            const tenants = await query("SELECT * FROM tenant WHERE room_id = ?", [roomId]);
            if (tenants.length === 0) {
                return res.status(400).json({
                    error: true,
                    message: "ไม่สามารถเปลี่ยนเป็น 'ไม่ว่าง' ได้ เนื่องจากยังไม่มีผู้เช่าในห้องนี้"
                });
            }
        }

        await query("UPDATE room SET status = ? WHERE room_id = ?", [status, roomId]);
        res.json({ error: false, message: "Update status success" });
        
    } catch (err) {
        res.status(500).json({ error: true, message: err.message });
    }
});

app.delete("/room/:id", async (req, res) => {
    try {
        const roomId = req.params.id;

        // 1. ลบ utility bill
        await query(
            "DELETE FROM utility_bill WHERE room_id = ?",
            [roomId]
        );

        // 2. ลบ tenant
        await query(
            "DELETE FROM tenant WHERE room_id = ?",
            [roomId]
        );


        // 3. ลบ room
        const result = await query(
            "DELETE FROM room WHERE room_id = ?",
            [roomId]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                error: true,
                message: "Room not found"
            });
        }

        res.json({
            error: false,
            message: "Delete room success"
        });

    } catch (err) {
        console.error("DELETE ROOM ERROR:", err);
        res.status(500).json({
            error: true,
            message: err.message
        });
    }
});


// 🔹 ดึงห้องทั้งหมด (แก้ไขให้กรองตาม owner_id)
app.get("/room", async (req, res) => {  try {
    const owner_id = req.query.owner_id; // ดึงค่าจากที่แอปส่งมา
    const results = await query(`
      SELECT r.*, b.building_name
      FROM room r
      LEFT JOIN building b ON r.building_id = b.building_id
      WHERE r.owner_id = ? 
    `, [owner_id]); // เพิ่ม WHERE กรองที่นี่

    res.json(results);
  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});


// =======================
// 👤 GET all tenants
// =======================
app.get("/tenants", async (req, res) => {
  try {

    const results = await query(`
      SELECT 
        t.*,
        r.room_number,
        b.building_name,
        u.username
      FROM tenant t
      LEFT JOIN room r
        ON t.room_id = r.room_id
      LEFT JOIN building b
        ON t.building_id = b.building_id
      LEFT JOIN user u
        ON t.user_id = u.user_id
    `);

    res.json(results);

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

// =======================
// 👤 GET tenant by id
// =======================
app.get("/tenants/:id", async (req, res) => {
  try {

    const results = await query(`
      SELECT 
        tenant.*,
        room.room_number
      FROM tenant
      LEFT JOIN room 
      ON tenant.room_id = room.room_id
      WHERE tenant.tenant_id = ?
    `, [req.params.id]);

    if (results.length === 0) {
      return res.status(404).json({
        success: false,
        message: "Tenant not found"
      });
    }

    res.json(results[0]);

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

// =======================
// ➕ CREATE tenant
// =======================
app.post("/tenants", async (req, res) => {
  try {

    const {
      first_name,
      last_name,
      phone,
      check_in_date,
      check_out_date,
      room_id,
      email
    } = req.body;

    const result = await query(
      `INSERT INTO tenant 
      (first_name, last_name, phone, check_in_date, check_out_date, room_id, email)
      VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [
        first_name,
        last_name,
        phone,
        check_in_date,
        check_out_date,
        room_id,
        email
      ]
    );

    res.status(201).json({
      success: true,
      message: "Tenant created",
      tenant_id: result.insertId
    });

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

// =======================
// ✏ UPDATE tenant
// =======================
app.put("/tenants/:id", async (req, res) => {
  try {

    const {
      first_name,
      last_name,
      phone,
      check_in_date,
      check_out_date,
      room_id,
      email
    } = req.body;

    await query(
      `UPDATE tenant SET
        first_name = ?,
        last_name = ?,
        phone = ?,
        check_in_date = ?,
        check_out_date = ?,
        room_id = ?,
        email = ?
      WHERE tenant_id = ?`,
      [
        first_name,
        last_name,
        phone,
        check_in_date,
        check_out_date,
        room_id,
        email,
        req.params.id
      ]
    );

    res.json({
      success: true,
      message: "Tenant updated"
    });

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

app.post("/insertTenant", async (req, res) => {
  try {

    const {
      first_name,
      last_name,
      phone,
      email,
      check_in_date,
      check_out_date,
      room_id
    } = req.body;

    const sql = `
      INSERT INTO tenant
      (first_name,last_name,phone,email,check_in_date,check_out_date,room_id,building_id,owner_id)
      SELECT
      ?,?,?,?,?,?,
      room_id,
      building_id,
      owner_id
      FROM room
      WHERE room_id = ?
    `;

    const result = await query(sql, [
      first_name,
      last_name,
      phone,
      email,
      check_in_date,
      check_out_date,
      room_id
    ]);

    res.json({
      success: true,
      tenant_id: result.insertId
    });

  } catch (err) {

    console.error("Insert tenant error:", err);

    res.status(500).json({
      success:false,
      message:err.message
    });

  }
});


app.post("/loginOwner", async (req,res)=>{

 const {username,password} = req.body

 const owner = await query(
  "SELECT * FROM owner WHERE username=? AND password=?",
  [username,password]
 )

 if(owner.length === 0){
  return res.send({success:false})
 }

 res.send({
  success:true,
  owner_id: owner[0].owner_id,
  dorm_name: owner[0].dorm_name
 })

})
app.post("/addBuilding", async (req,res)=>{

 const {owner_id,building_name} = req.body

 await query(
  "INSERT INTO building (owner_id,building_name) VALUES (?,?)",
  [owner_id,building_name]
 )

 res.send({success:true})

})
app.post("/addRoomType", async (req,res)=>{

 const {owner_id,type_name,price} = req.body

 await query(
  "INSERT INTO room_type (owner_id,type_name,price) VALUES (?,?,?)",
  [owner_id,type_name,price]
 )

 res.send({success:true})

})
app.post("/generateRooms", async (req,res)=>{

 const {owner_id,building_id,floors,rooms_per_floor,password} = req.body

 for(let f=1; f<=floors; f++){

   for(let r=1; r<=rooms_per_floor; r++){

     const roomNumber = `${f}${String(r).padStart(2,'0')}`

     const room = await query(
       "INSERT INTO room (room_number,status,building_id,owner_id) VALUES (?,?,?,?)",
       [roomNumber,"ว่าง",building_id,owner_id]
     )

     const hash = await bcrypt.hash(password,10)

     await query(
       "INSERT INTO user (username,password,role) VALUES (?,?,?)",
       [roomNumber,hash,"tenant"]
     )

   }

 }

 res.send({success:true})

})


app.get("/rooms/:ownerId", async (req, res) => {

  const ownerId = req.params.ownerId;

  const rooms = await db.query(
    "SELECT * FROM room WHERE owner_id = ?",
    [ownerId]
  );

  res.json(rooms);
});

app.post("/registerowner", async (req, res) => {

  console.log("BODY:", req.body)

  const {
    username,
    password,
    dorm_name,
    address,
    phone,
    buildings,
    room_types
  } = req.body
  console.log("USERNAME:", username)
console.log("BUILDINGS:", buildings)
console.log("ROOM TYPES:", room_types)

  try {

    if (!username || !password || !dorm_name) {
      return res.status(400).json({
        success: false,
        message: "Missing required fields"
      })
    }

    const hash = await bcrypt.hash(password, 10)

    // =====================
    // CREATE USER
    // =====================
    const userResult = await query(
      "INSERT INTO user (username,password,role) VALUES (?,?,?)",
      [username, hash, "admin"]
    )

    const userId = userResult.insertId
    console.log("User ID:", userId)

    // =====================
    // CREATE OWNER
    // =====================
    const ownerResult = await query(
      "INSERT INTO owner (username,password,dorm_name) VALUES (?,?,?)",
      [username, hash, dorm_name]
    )

    const ownerId = ownerResult.insertId
    console.log("Owner ID:", ownerId)

    // =====================
    // UPDATE USER -> OWNER_ID
    // =====================
    await query(
      "UPDATE user SET owner_id=? WHERE user_id=?",
      [ownerId, userId]
    )

    // =====================
    // CREATE DORMITORY
    // =====================
    const dormResult = await query(
      "INSERT INTO dormitory (dorm_name,address,phone,user_id) VALUES (?,?,?,?)",
      [dorm_name, address, phone, userId]
    )

    const dormId = dormResult.insertId
    console.log("Dorm ID:", dormId)

    console.log("Buildings:", buildings)
    console.log("Room Types:", room_types)

    // =====================
    // INSERT BUILDINGS
    // =====================
    if (Array.isArray(buildings)) {

      for (const b of buildings) {

        let buildingName = b.name

        if (typeof buildingName === "object") {
          buildingName = buildingName.value
        }

        if (!buildingName || buildingName.trim() === "") continue

        console.log("Insert building:", buildingName)

        await query(
          "INSERT INTO building (building_name,dorm_id,owner_id) VALUES (?,?,?)",
          [buildingName, dormId, ownerId]
        )
      }

    }

    // =====================
    // INSERT ROOM TYPES
    // =====================
    if (Array.isArray(room_types)) {

      for (const r of room_types) {

        let typeName = r.name
        let price = r.price

        if (typeof typeName === "object") {
          typeName = typeName.value
        }

        if (typeof price === "object") {
          price = price.value
        }

        if (!typeName || typeName.trim() === "") continue

        console.log("Insert room type:", typeName, price)

        await query(
          "INSERT INTO room_type (type_name,price,owner_id) VALUES (?,?,?)",
          [typeName, Number(price || 0), ownerId]
        )
      }

    }

    // =====================
    // SUCCESS
    // =====================
    res.json({
      success: true,
      message: "Register owner success"
    })

  } catch (err) {

    console.log("REGISTER ERROR:", err)

    res.status(500).json({
      success: false,
      error: err.message
    })

  }

})

// 1. เพิ่ม Route นี้เพื่อแปลง user_id เป็น owner_id
app.get("/owner_id/:user_id", async (req, res) => {

  try {

    const user_id = req.params.user_id

    const results = await query(
      "SELECT owner_id FROM user WHERE user_id = ?",
      [user_id]
    )

    if(results.length === 0){
      return res.status(404).json({
        error:true,
        message:"Owner not found"
      })
    }

    res.json({
      owner_id: results[0].owner_id
    })

  } catch(err){

    res.status(500).json({
      error:true,
      message:err.message
    })

  }

})

// 2. เพิ่ม Route นี้เพื่อดึงข้อมูล "อาคาร" สำหรับ Dropdown
app.get("/buildings", async (req, res) => {
  try {
    const owner_id = req.query.owner_id;
    if (!owner_id) {
      return res.status(400).json({ error: true, message: "Owner ID is required" });
    }
    const results = await query("SELECT * FROM building WHERE owner_id = ?", [owner_id]);
    res.json(results);

  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});

// 3. เพิ่ม Route นี้เพื่อดึงข้อมูล "ประเภทห้อง" สำหรับ Dropdown
app.get("/roomTypes", async (req, res) => {
  try {
    const owner_id = req.query.owner_id;
    if (!owner_id) {
      return res.status(400).json({ error: true, message: "Owner ID is required" });
    }
    const results = await query("SELECT * FROM room_type WHERE owner_id = ?", [owner_id]);
    res.json(results);
    
  } catch (err) {
    res.status(500).json({ error: true, message: err.message });
  }
});
// ===========================
// CREATE TENANT ACCOUNT
// ===========================

app.post("/createTenantAccount", async (req, res) => {

  const { username, password, role, tenant_id } = req.body

  try {

    const hash = await bcrypt.hash(password, 10)

    // หา owner จาก tenant
    const owner = await query(`
      SELECT owner.owner_id
      FROM tenant
      JOIN room ON tenant.room_id = room.room_id
      JOIN building ON room.building_id = building.building_id
      JOIN owner ON building.owner_id = owner.owner_id
      WHERE tenant.tenant_id = ?
    `,[tenant_id])

    const owner_id = owner[0].owner_id

    // create user
    const userResult = await query(
      "INSERT INTO user (username,password,role,owner_id) VALUES (?,?,?,?)",
      [username,hash,role,owner_id]
    )

    const user_id = userResult.insertId

    // update tenant
    await query(
      "UPDATE tenant SET user_id=? WHERE tenant_id=?",
      [user_id,tenant_id]
    )

    res.send({
      success:true
    })

  } catch(err) {

    console.log(err)

    res.send({
      success:false
    })

  }

})
app.get("/bill/user/:user_id", async (req, res) => {

  const user_id = req.params.user_id;

  try {

    const sql = `
      SELECT 
        b.bill_id,
        b.user_id,
        b.room_id,
        b.bill_month,
        b.bill_year,
        b.water_unit,
        b.electric_unit,
        r.room_type_id,
        rt.price AS rent_price
      FROM utility_bill b
      JOIN room r ON b.room_id = r.room_id
      JOIN room_type rt ON r.room_type_id = rt.room_type_id
      WHERE b.user_id = ?
      ORDER BY b.bill_year DESC, b.bill_month DESC
      LIMIT 1
    `;

    const result = await query(sql, [user_id]);

    if (result.length === 0) {
      return res.json(null);
    }

    res.json(result[0]);

  } catch (err) {

    console.log(err);
    res.status(500).json({ error: err.message });

  }

});

app.post("/payment", async (req, res) => {

  const {
    payment_date,
    payment_month,
    payment_year,
    amount,
    payment_method,
    slip_image,
    user_id
  } = req.body;

  try {

    await query(
      `INSERT INTO payment
      (payment_date, payment_month, payment_year, amount, payment_method, slip_image, user_id)
      VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [
        payment_date,
        payment_month,
        payment_year,
        amount,
        payment_method,
        slip_image,
        user_id
      ]
    );

    res.json({ success: true });

  } catch (err) {
    res.status(500).json({ error: err.message });
  }

});

const multer = require("multer")
const path = require("path")

const storage = multer.diskStorage({

  destination: function (req, file, cb) {
    cb(null, "uploads/")
  },

  filename: function (req, file, cb) {

    const unique =
      Date.now() + "-" + Math.round(Math.random() * 1E9)

    cb(null, unique + path.extname(file.originalname))

  }

})

const upload = multer({ storage })

app.use("/uploads", express.static("uploads"))

app.post("/upload-slip", upload.single("slip"), async (req,res)=>{

  try{

    const {amount,month,year,user_id} = req.body

    const slip = req.file.filename

    await query(
      `INSERT INTO payment
      (payment_date,payment_month,payment_year,amount,payment_method,slip_image,user_id)
      VALUES (NOW(),?,?,?,?,?,?)`,
      [
        month,
        year,
        amount,
        "transfer",
        slip,
        user_id
      ]
    )

    res.json({success:true})

  }catch(err){

    res.status(500).json({error:err.message})

  }

})

app.post("/createBill", async (req, res) => {
  const { room_id, water_unit, electric_unit, total_amount, month, year, owner_id } = req.body;

  try {
    // 1. หา user_id ของผู้เช่าปัจจุบันที่ผูกกับห้องนี้ (ดึงจากตาราง tenant หรือ user ที่มี owner_id ตรงกัน)
    const tenantInfo = await query(
      "SELECT user_id FROM tenant WHERE room_id = ? LIMIT 1",
      [room_id]
    );

    if (tenantInfo.length === 0 || !tenantInfo[0].user_id) {
      return res.json({ status: "error", message: "ไม่พบผู้เช่าในห้องนี้" });
    }

    const user_id = tenantInfo[0].user_id;

    // 2. Insert ลง utility_bill ให้ครบทุก Column
    const sql = `
      INSERT INTO utility_bill 
      (room_id, bill_month, bill_year, water_unit, electric_unit, total_amount, status, user_id, owner_id) 
      VALUES (?, ?, ?, ?, ?, ?, 'รอตรวจสอบ', ?, ?)
    `;
    
    await query(sql, [
      room_id, month, year, water_unit, electric_unit, total_amount, user_id, owner_id
    ]);

    res.json({ success: true, message: "สร้างบิลสำเร็จ" });
  } catch (err) {
    console.error(err);
    res.status(500).json({ success: false, error: err.message });
  }
});
/* ===============================
   UPLOAD SLIP API
================================= */

app.post("/uploadSlip", upload.single("slip"), async (req, res) => {

  try {

    const { bill_id } = req.body

    if (!bill_id) {
      return res.json({
        status: "error",
        message: "กรุณาส่ง bill_id"
      })
    }

    const slip = req.file ? req.file.filename : null

    /* =========================
       หา bill
    ========================== */

    const bill = await query(
      "SELECT user_id, total_amount, bill_month, bill_year FROM utility_bill WHERE bill_id = ?",
      [bill_id]
    )

    if (bill.length === 0) {
      return res.json({
        status: "error",
        message: "ไม่พบบิล"
      })
    }

    const user_id = bill[0].user_id
    const amount = bill[0].total_amount
    const month = bill[0].bill_month
    const year = bill[0].bill_year

    /* =========================
       INSERT PAYMENT
    ========================== */

    const insertSql = `
      INSERT INTO payment
      (payment_date, payment_month, payment_year, amount, payment_method, slip_image, user_id)
      VALUES (NOW(), ?, ?, ?, 'transfer', ?, ?)
    `

    await query(insertSql, [
      month,
      year,
      amount,
      slip,
      user_id
    ])

    /* =========================
       UPDATE STATUS BILL
    ========================== */

    await query(
      "UPDATE utility_bill SET status='รอตรวจสอบ' WHERE bill_id=?",
      [bill_id]
    )

    res.json({
      status: "success",
      message: "อัปโหลดสลิปสำเร็จ"
    })

  } catch (error) {

    console.log(error)

    res.json({
      status: "error",
      message: "server error"
    })
  }

})

app.get("/bill/:billId", (req, res) => {

    const billId = req.params.billId;

    const sql = `
        SELECT 
            ub.bill_id,
            ub.room_id,
            r.room_number,
            ub.water_unit,
            ub.electric_unit,
            ub.total_amount as amount,
            rt.price as room_price,
            ub.status,
            ub.bill_month,
            ub.bill_year
        FROM utility_bill ub
        JOIN room r ON ub.room_id = r.room_id
        JOIN room_type rt ON r.room_type_id = rt.room_type_id
        WHERE ub.bill_id = ?
    `;

    db.query(sql, [billId], (err, result) => {

        if (err) {
            return res.status(500).json(err);
        }

        res.json(result[0]);
    });

});

// =======================
// 🔎 GET tenant by room_id
// =======================
app.get("/tenantByRoom/:room_id", async (req, res) => {

  try {

    const room_id = req.params.room_id

    const rows = await query(
      "SELECT * FROM tenant WHERE room_id = ? LIMIT 1",
      [room_id]
    )

    if (rows.length > 0) {

      res.json({
        exists: true,
        tenant: rows[0]
      })

    } else {

      res.json({
        exists: false,
        tenant: null
      })

    }

  } catch(err){

    res.status(500).json({
      error:true,
      message:err.message
    })

  }

})

// =======================
// GET TENANT ACCOUNT
// =======================

app.get("/tenantAccount/:tenant_id", async (req,res)=>{

  try{

    const tenant_id = req.params.tenant_id

    const result = await query(`
      SELECT u.user_id,u.username
      FROM user u
      JOIN tenant t ON u.user_id = t.user_id
      WHERE t.tenant_id = ?
      LIMIT 1
    `,[tenant_id])

    if(result.length === 0){

      return res.json({
        exists:false
      })

    }

    res.json({
      exists:true,
      user_id: result[0].user_id,
      username: result[0].username
    })

  }catch(err){

    res.status(500).json({
      error:true,
      message:err.message
    })

  }

})

// =======================
// UPDATE TENANT ACCOUNT
// =======================

app.put("/tenantAccount/:tenant_id", async (req,res)=>{

  try{

    const tenant_id = req.params.tenant_id
    const {username,password} = req.body

    const tenant = await query(
      "SELECT user_id FROM tenant WHERE tenant_id=?",
      [tenant_id]
    )

    if(tenant.length === 0){

      return res.send({
        success:false
      })

    }

    const user_id = tenant[0].user_id

    let updateData = {}

    if(username){
      updateData.username = username
    }

    if(password){
      const hash = await bcrypt.hash(password,10)
      updateData.password = hash
    }

    await query(
      "UPDATE user SET ? WHERE user_id=?",
      [updateData,user_id]
    )

    res.send({
      success:true
    })

  }catch(err){

    res.status(500).send({
      success:false,
      message:err.message
    })

  }

})

app.get("/payment/user/:user_id", async (req, res) => {

  const user_id = req.params.user_id
  const month = new Date().getMonth() + 1
  const year = new Date().getFullYear()

  const sql = `
    SELECT *
    FROM payment
    WHERE user_id = ?
    AND payment_month = ?
    AND payment_year = ?
    LIMIT 1
  `

  const result = await query(sql,[user_id,month,year])

  if(result.length === 0){
      return res.json(null)
  }

  res.json(result[0])

})


// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});