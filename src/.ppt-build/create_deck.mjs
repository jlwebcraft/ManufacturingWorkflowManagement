import fs from "node:fs/promises";
import path from "node:path";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const ROOT = "C:\\Programming\\SEM2\\ManufacturingWorkFlowManagement\\src";
const OUT_DIR = path.join(ROOT, ".ppt-build", "output");
const FINAL_PPTX = path.join(ROOT, "Manufacturing_Workflow_Management_Presentation.pptx");

const W = 1280;
const H = 720;
const C = {
  ink: "#111827",
  muted: "#64748B",
  line: "#CBD5E1",
  faint: "#F8FAFC",
  blue: "#2563EB",
  teal: "#0F766E",
  green: "#15803D",
  amber: "#B45309",
  red: "#B91C1C",
  purple: "#6D28D9",
  pink: "#BE185D",
  slate: "#334155",
  white: "#FFFFFF",
};

function pos(left, top, width, height) {
  return { left, top, width, height };
}

function addText(slide, text, p, style = {}) {
  const box = slide.shapes.add({
    geometry: "textbox",
    position: p,
    fill: "none",
    line: { style: "solid", fill: "none", width: 0 },
  });
  box.text = text;
  box.text.style = {
    fontFace: "Aptos",
    fontSize: 20,
    color: C.ink,
    ...style,
  };
  return box;
}

function addShape(slide, text, p, fill, line = C.line, style = {}) {
  const shape = slide.shapes.add({
    geometry: style.geometry || "roundRect",
    position: p,
    fill,
    line: { style: "solid", fill: line, width: style.lineWidth ?? 1.2 },
    borderRadius: style.borderRadius || "rounded-md",
  });
  if (text) {
    shape.text = text;
    shape.text.style = {
      fontFace: "Aptos",
      fontSize: style.fontSize || 18,
      bold: style.bold ?? false,
      color: style.color || C.ink,
      align: style.align || "center",
    };
  }
  return shape;
}

function addHeader(slide, title, kicker = "MANUFACTURING WORKFLOW MANAGEMENT") {
  addText(slide, kicker, pos(64, 36, 600, 26), {
    fontSize: 13,
    bold: true,
    color: C.muted,
  });
  addText(slide, title, pos(64, 72, 1030, 64), {
    fontSize: 38,
    bold: true,
    color: C.ink,
  });
  slide.shapes.add({
    geometry: "line",
    position: pos(64, 146, 1152, 0),
    fill: "none",
    line: { style: "solid", fill: C.line, width: 1.4 },
  });
}

function addFooter(slide, n) {
  addText(slide, `Java | DBMS | Data Structures    ${n}`, pos(64, 674, 500, 24), {
    fontSize: 12,
    color: C.muted,
  });
}

function addBullets(slide, items, p, opts = {}) {
  const text = items.map((item) => `- ${item}`).join("\n");
  return addText(slide, text, p, {
    fontSize: opts.fontSize || 22,
    color: opts.color || C.ink,
    breakLine: true,
    lineSpacingMultiple: opts.lineSpacingMultiple || 1.15,
  });
}

function addNotes(slide, lines) {
  slide.speakerNotes.textFrame.setText([
    "[Sources]",
    "Local project source code in C:\\Programming\\SEM2\\ManufacturingWorkFlowManagement\\src.",
    ...lines.map((line) => `- ${line}`),
  ].join("\n"));
  slide.speakerNotes.setVisible(true);
}

function connect(slide, from, to, color = C.slate) {
  slide.shapes.connect(from, to, {
    kind: "straight",
    line: { style: "solid", fill: color, width: 1.6 },
    head: "triangle",
  });
}

function workflowNode(slide, label, x, y, w, h, fill, line, fontSize = 15) {
  return addShape(slide, label, pos(x, y, w, h), fill, line, {
    fontSize,
    bold: true,
    color: C.ink,
  });
}

async function main() {
  await fs.mkdir(OUT_DIR, { recursive: true });
  const presentation = Presentation.create({
    slideSize: { width: W, height: H },
  });

  // 1
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addText(slide, "Manufacturing Workflow\nManagement System", pos(72, 90, 760, 150), {
      fontSize: 52,
      bold: true,
      color: C.ink,
    });
    addText(slide, "PC manufacturing project demonstrating Java, DBMS and data-structure concepts through one connected production workflow.", pos(76, 258, 760, 78), {
      fontSize: 24,
      color: C.slate,
    });
    const a = workflowNode(slide, "Product", 780, 118, 150, 56, "#EFF6FF", C.blue, 17);
    const b = workflowNode(slide, "BOM", 972, 118, 130, 56, "#FEF3C7", C.amber, 17);
    const c = workflowNode(slide, "Inventory", 780, 244, 150, 56, "#ECFDF5", C.green, 17);
    const d = workflowNode(slide, "Production", 972, 244, 150, 56, "#F5F3FF", C.purple, 17);
    const e = workflowNode(slide, "Inspection", 780, 370, 150, 56, "#F0FDFA", C.teal, 17);
    const f = workflowNode(slide, "Delivery", 972, 370, 150, 56, "#FFF1F2", C.pink, 17);
    connect(slide, a, b, C.blue);
    connect(slide, b, d, C.amber);
    connect(slide, c, d, C.green);
    connect(slide, d, e, C.purple);
    connect(slide, e, f, C.teal);
    addShape(slide, "Core idea: every production step leaves a database record.", pos(72, 512, 820, 64), "#F8FAFC", C.line, {
      fontSize: 22,
      bold: true,
      align: "left",
    });
    addFooter(slide, 1);
    addNotes(slide, ["app/Main.java", "menu/MainMenu.java", "database/DatabaseInitializer.java"]);
  }

  // 2
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Real-World Problem Solved");
    const columns = [
      ["Manual tracking", "Raw material stock can be wrong, late, or duplicated."],
      ["Production delays", "Orders wait when BOM, inventory, workers, or machines are unclear."],
      ["Poor traceability", "Failed inspection and delivery status are hard to follow without history."],
    ];
    columns.forEach(([t, d], i) => {
      const x = 78 + i * 390;
      addShape(slide, t, pos(x, 205, 330, 70), ["#EFF6FF", "#FEF3C7", "#FEE2E2"][i], [C.blue, C.amber, C.red][i], { fontSize: 23, bold: true });
      addText(slide, d, pos(x + 18, 300, 294, 116), { fontSize: 21, color: C.slate });
    });
    addShape(slide, "The project converts scattered manufacturing work into one controlled workflow: plan, source, produce, inspect, store and deliver.", pos(112, 498, 1056, 82), "#F8FAFC", C.line, {
      fontSize: 25,
      bold: true,
      color: C.ink,
    });
    addFooter(slide, 2);
    addNotes(slide, ["Project workflow implemented through menu and manager classes.", "workflow_history table records stage changes."]);
  }

  // 3
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "End-to-End Manufacturing Workflow");
    const labels = [
      "Category", "Product\nPlanning", "BOM", "Buy\nDepartment", "Raw Material\nInventory",
      "Production\nOrder", "Machine\nAllocation", "Manufacturing", "Quality\nInspection", "Finished\nGoods", "Delivery",
    ];
    const fills = ["#F5F3FF", "#EFF6FF", "#FEF3C7", "#FFF7ED", "#ECFDF5", "#F8FAFC", "#E0F2FE", "#F0FDFA", "#DBEAFE", "#DCFCE7", "#FFE4E6"];
    const lines = [C.purple, C.blue, C.amber, C.amber, C.green, C.slate, C.blue, C.teal, C.blue, C.green, C.pink];
    const nodes = [];
    labels.forEach((label, i) => {
      const x = i < 6 ? 68 + i * 190 : [1018, 828, 638, 448, 258][i - 6];
      const y = i < 6 ? 220 : 420;
      nodes.push(workflowNode(slide, label, x, y, 142, 64, fills[i], lines[i], 14));
    });
    for (let i = 0; i < 5; i++) connect(slide, nodes[i], nodes[i + 1]);
    connect(slide, nodes[5], nodes[6]);
    for (let i = 6; i < nodes.length - 1; i++) connect(slide, nodes[i], nodes[i + 1]);
    slide.shapes.connect(nodes[8], nodes[5], {
      kind: "elbow",
      line: { style: "dash", fill: C.red, width: 1.6 },
      head: "triangle",
    });
    addText(slide, "If inspection fails, a replacement/rework production order is queued again.", pos(664, 334, 480, 46), {
      fontSize: 18,
      color: C.red,
      bold: true,
    });
    addFooter(slide, 3);
    addNotes(slide, ["QualityInspectionManager.java", "ProductionOrderManager.java", "DeliveryManager.java"]);
  }

  // 4
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Main Modules in the Console System");
    const modules = [
      ["User and roles", "Login users, workers, creators and access responsibility."],
      ["Product setup", "Categories, products and BOM material requirements."],
      ["Buy Department", "Single purpose: place raw material purchase entries."],
      ["Inventory", "Raw material stock, finished goods and stock movements."],
      ["Machines", "Availability, status and production resource allocation."],
      ["Production", "Order creation, queue priority, progress and material usage."],
      ["Quality", "Inspection result, defective quantity and rework flow."],
      ["Delivery and reports", "Ship approved goods and generate summary reports."],
    ];
    modules.forEach(([t, d], i) => {
      const x = 72 + (i % 4) * 290;
      const y = 202 + Math.floor(i / 4) * 190;
      addShape(slide, t, pos(x, y, 248, 54), i % 2 === 0 ? "#F8FAFC" : "#F0FDFA", i % 2 === 0 ? C.line : C.teal, { fontSize: 19, bold: true });
      addText(slide, d, pos(x + 14, y + 70, 220, 76), { fontSize: 17, color: C.slate });
    });
    addFooter(slide, 4);
    addNotes(slide, ["menu package classes", "manager package classes"]);
  }

  // 5
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Database Schema Backbone");
    const groups = [
      ["Master Data", ["roles", "users", "product_categories", "products", "raw_materials", "product_materials"]],
      ["Operations", ["inventory", "finished_goods_inventory", "material_transactions", "production_orders", "machines", "worker_assignments"]],
      ["Control and Output", ["production_material_usage", "workflow_history", "quality_inspections", "deliveries"]],
    ];
    groups.forEach(([title, tables], i) => {
      const x = 80 + i * 390;
      addShape(slide, title, pos(x, 202, 322, 56), ["#EFF6FF", "#ECFDF5", "#FFF7ED"][i], [C.blue, C.green, C.amber][i], { fontSize: 23, bold: true });
      tables.forEach((table, j) => {
        addShape(slide, table, pos(x, 286 + j * 46, 322, 34), C.white, C.line, { fontSize: 15, geometry: "rect" });
      });
    });
    addText(slide, "16 normalized tables support the workflow from product planning to delivery.", pos(80, 590, 880, 34), {
      fontSize: 22,
      bold: true,
      color: C.ink,
    });
    addFooter(slide, 5);
    addNotes(slide, ["database/DatabaseInitializer.java creates the schema.", "database/DatabaseSeeder.java adds PC manufacturing seed data."]);
  }

  // 6
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "DBMS Concepts Demonstrated");
    const left = [
      "Primary keys for every entity",
      "Foreign keys between products, BOM, orders, workers and inspections",
      "UNIQUE constraints for usernames, category names, material names and order numbers",
      "NOT NULL fields for required business data",
      "CHECK rules for stock levels, quantities, status and prices",
    ];
    const right = [
      "CRUD in every major module",
      "JOIN queries for readable reports",
      "GROUP BY and HAVING for summary reports",
      "Subqueries for filtered operational views",
      "Database metadata and result-set metadata examples",
    ];
    addShape(slide, "Constraints and relationships", pos(82, 198, 500, 56), "#EFF6FF", C.blue, { fontSize: 23, bold: true });
    addBullets(slide, left, pos(104, 278, 456, 220), { fontSize: 19 });
    addShape(slide, "Query concepts", pos(696, 198, 500, 56), "#ECFDF5", C.green, { fontSize: 23, bold: true });
    addBullets(slide, right, pos(718, 278, 456, 220), { fontSize: 19 });
    addFooter(slide, 6);
    addNotes(slide, ["database/DatabaseInitializer.java", "manager/ReportManager.java", "menu/WorkflowConceptMenu.java"]);
  }

  // 7
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Transactions, Procedures and ACID");
    const steps = [
      ["SAVEPOINT", "Protects multi-step operations before stock is consumed."],
      ["COMMIT", "Finalizes successful order, inspection or delivery changes."],
      ["ROLLBACK", "Restores database state when a workflow step fails."],
      ["Trigger", "Automatically records audit-style workflow changes."],
      ["Stored procedure", "CallableStatement runs database-side workflow operations."],
      ["Stored function", "Returns calculated values used by reports or checks."],
    ];
    steps.forEach(([t, d], i) => {
      const x = i < 3 ? 86 : 688;
      const y = 190 + (i % 3) * 132;
      addShape(slide, t, pos(x, y, 220, 50), ["#FEF3C7", "#DCFCE7", "#FEE2E2", "#F5F3FF", "#DBEAFE", "#F0FDFA"][i], [C.amber, C.green, C.red, C.purple, C.blue, C.teal][i], { fontSize: 20, bold: true });
      addText(slide, d, pos(x + 244, y + 6, 270, 54), { fontSize: 18, color: C.slate });
    });
    addShape(slide, "ACID in this project: order, material consumption, inspection and delivery updates are treated as one reliable unit of work.", pos(104, 596, 1068, 50), "#F8FAFC", C.line, { fontSize: 20, bold: true });
    addFooter(slide, 7);
    addNotes(slide, ["manager/ProductionOrderManager.java", "manager/QualityInspectionManager.java", "manager/DeliveryManager.java", "database/DatabaseInitializer.java"]);
  }

  // 8
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Java Concepts Used");
    const concepts = [
      ["Abstract class", "Shared workflow/report behavior"],
      ["Interface", "Common service contracts"],
      ["Lambda + Predicate", "Filtering active records and validations"],
      ["Custom exception", "Readable business-rule errors"],
      ["HashMap / HashSet", "Fast lookup and duplicate prevention"],
      ["Vector", "Legacy synchronized collection example"],
      ["File IO", "Report export with readers and writers"],
      ["DateTimeFormatter", "Human-readable timestamps"],
      ["Synchronization", "Protects shared workflow operations"],
      ["CallableStatement", "Calls stored procedures/functions"],
    ];
    concepts.forEach(([t, d], i) => {
      const x = 72 + (i % 2) * 575;
      const y = 186 + Math.floor(i / 2) * 86;
      addShape(slide, t, pos(x, y, 220, 42), i % 2 === 0 ? "#EFF6FF" : "#F0FDFA", i % 2 === 0 ? C.blue : C.teal, { fontSize: 17, bold: true });
      addText(slide, d, pos(x + 238, y + 7, 292, 36), { fontSize: 17, color: C.slate });
    });
    addFooter(slide, 8);
    addNotes(slide, ["menu/WorkflowConceptMenu.java", "manager package", "utils/ConsoleFormatter.java", "exceptions package"]);
  }

  // 9
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Object-Oriented Design");
    const layers = [
      ["Model", "Product, User, RawMaterial, Inventory and ProductionOrder store entity data."],
      ["Manager", "Business rules: create orders, consume stock, inspect quality and deliver goods."],
      ["Menu", "Console interaction layer that calls manager methods."],
      ["Database", "Connection, initializer, seed data and SQL workflow objects."],
    ];
    layers.forEach(([name, desc], i) => {
      const y = 210 + i * 92;
      const box = addShape(slide, name, pos(104, y, 220, 54), ["#F5F3FF", "#EFF6FF", "#ECFDF5", "#FFF7ED"][i], [C.purple, C.blue, C.green, C.amber][i], { fontSize: 21, bold: true });
      addText(slide, desc, pos(360, y + 9, 720, 42), { fontSize: 21, color: C.slate });
      if (i > 0) connect(slide, layers[i - 1].shape || box, box, C.line);
      layers[i].shape = box;
    });
    addText(slide, "Separation of concerns makes the project easier to test, extend and explain.", pos(104, 594, 900, 36), {
      fontSize: 23,
      bold: true,
      color: C.ink,
    });
    addFooter(slide, 9);
    addNotes(slide, ["model package", "manager package", "menu package", "database package"]);
  }

  // 10
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Data Structures Package");
    const rows = [
      ["LinkedList", "Production history / waiting list", "Insert, delete first, delete last, delete specific node"],
      ["Stack", "Workflow undo", "Push, pop, peek, display"],
      ["Queue", "FIFO production steps", "Enqueue, dequeue, front"],
      ["PriorityQueue", "Urgent production orders", "Add by priority, remove highest priority"],
      ["HashMap", "Fast product or machine lookup", "Put, get, remove, search"],
      ["HashSet", "Avoid duplicate IDs/names", "Add, remove, contains"],
      ["Vector", "Synchronized dynamic list example", "Add, get, update, delete"],
      ["BST", "Sorted searchable catalog", "Insert, search, inorder traversal"],
    ];
    rows.forEach((row, i) => {
      const y = 184 + i * 52;
      addShape(slide, row[0], pos(70, y, 190, 36), i % 2 ? "#F8FAFC" : "#EFF6FF", i % 2 ? C.line : C.blue, { fontSize: 16, bold: true, geometry: "rect" });
      addText(slide, row[1], pos(282, y + 5, 330, 28), { fontSize: 16, color: C.slate });
      addText(slide, row[2], pos(650, y + 5, 520, 28), { fontSize: 16, color: C.slate });
    });
    addFooter(slide, 10);
    addNotes(slide, ["DSA/LinkedList.java", "DSA/Stack.java", "DSA/Queue.java", "DSA/PriorityQueue.java", "DSA/HashMap.java", "DSA/HashSet.java", "DSA/Vector.java", "DSA/BinarySearchTree.java"]);
  }

  // 11
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "DSA Inside the Workflow");
    const pq = workflowNode(slide, "PriorityQueue\nurgent orders first", 94, 230, 210, 84, "#FEF3C7", C.amber, 17);
    const q = workflowNode(slide, "Queue\nnormal order flow", 382, 230, 210, 84, "#EFF6FF", C.blue, 17);
    const stack = workflowNode(slide, "Stack\nundo last step", 670, 230, 210, 84, "#FEE2E2", C.red, 17);
    const list = workflowNode(slide, "LinkedList\nworkflow history", 958, 230, 210, 84, "#ECFDF5", C.green, 17);
    connect(slide, pq, q, C.amber);
    connect(slide, q, stack, C.blue);
    connect(slide, stack, list, C.red);
    addShape(slide, "Hashing supports fast lookup for products, raw materials and machines. BST gives sorted/searchable catalog behavior for demonstration.", pos(160, 432, 960, 78), "#F8FAFC", C.line, {
      fontSize: 23,
      bold: true,
    });
    addFooter(slide, 11);
    addNotes(slide, ["menu/ProductionQueueMenu.java", "manager/ProductionQueueManager.java", "DSA package"]);
  }

  // 12
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Quality Inspection and Rework Logic");
    const made = workflowNode(slide, "Manufactured\nProduct", 120, 260, 190, 74, "#EFF6FF", C.blue, 17);
    const inspect = workflowNode(slide, "Quality\nInspection", 418, 260, 190, 74, "#F0FDFA", C.teal, 17);
    const pass = workflowNode(slide, "Approved", 742, 190, 170, 58, "#DCFCE7", C.green, 17);
    const fail = workflowNode(slide, "Defective", 742, 342, 170, 58, "#FEE2E2", C.red, 17);
    const fg = workflowNode(slide, "Finished Goods\nInventory", 1000, 190, 190, 58, "#ECFDF5", C.green, 16);
    const re = workflowNode(slide, "Replacement\nOrder", 1000, 342, 190, 58, "#FFF7ED", C.amber, 16);
    connect(slide, made, inspect, C.blue);
    connect(slide, inspect, pass, C.green);
    connect(slide, pass, fg, C.green);
    connect(slide, inspect, fail, C.red);
    connect(slide, fail, re, C.red);
    slide.shapes.connect(re, made, { kind: "elbow", line: { style: "dash", fill: C.amber, width: 1.6 }, head: "triangle" });
    addText(slide, "Business rule: good quantity enters finished goods; defective quantity is tracked and can create a new manufacturing order.", pos(136, 522, 970, 58), {
      fontSize: 23,
      color: C.ink,
      bold: true,
    });
    addFooter(slide, 12);
    addNotes(slide, ["manager/QualityInspectionManager.java", "quality_inspections table", "finished_goods_inventory table"]);
  }

  // 13
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Delivery and Reporting");
    addShape(slide, "Deliver order", pos(100, 206, 240, 70), "#FFE4E6", C.pink, { fontSize: 25, bold: true });
    addText(slide, "Select an approved/completed production order, confirm quantity and record customer/location details.", pos(102, 306, 430, 82), {
      fontSize: 22,
      color: C.slate,
    });
    addShape(slide, "Database updates", pos(548, 206, 260, 70), "#ECFDF5", C.green, { fontSize: 25, bold: true });
    addBullets(slide, [
      "Finished goods stock decreases",
      "Delivery record is inserted",
      "Production order becomes DELIVERED",
      "Workflow history receives an entry",
    ], pos(558, 306, 370, 150), { fontSize: 20 });
    addShape(slide, "Reports", pos(948, 206, 220, 70), "#EFF6FF", C.blue, { fontSize: 25, bold: true });
    addText(slide, "Production, inventory, quality and delivery reports help management review performance.", pos(944, 306, 238, 112), {
      fontSize: 22,
      color: C.slate,
    });
    addFooter(slide, 13);
    addNotes(slide, ["manager/DeliveryManager.java", "menu/DeliveryMenu.java", "manager/ReportManager.java"]);
  }

  // 14
  {
    const slide = presentation.slides.add();
    slide.background.fill = C.white;
    addHeader(slide, "Demo Flow and Future Scope");
    addShape(slide, "Suggested demo path", pos(82, 198, 500, 58), "#EFF6FF", C.blue, { fontSize: 24, bold: true });
    addBullets(slide, [
      "Add category and product",
      "Create BOM with raw materials",
      "Buy missing raw material",
      "Check inventory and create production order",
      "Assign machine and worker",
      "Inspect quality, update finished goods, deliver order",
    ], pos(104, 280, 448, 226), { fontSize: 19 });
    addShape(slide, "Future improvements", pos(696, 198, 500, 58), "#F5F3FF", C.purple, { fontSize: 24, bold: true });
    addBullets(slide, [
      "Customer order module",
      "Barcode based material receiving",
      "Graphical dashboard",
      "Predictive machine maintenance",
      "Stronger password and session controls",
      "Supplier comparison reports",
    ], pos(718, 280, 448, 226), { fontSize: 19 });
    addShape(slide, "Conclusion: the project is a practical mini-ERP for PC manufacturing, built around database-backed workflow control.", pos(112, 596, 1056, 50), "#F8FAFC", C.line, {
      fontSize: 21,
      bold: true,
    });
    addFooter(slide, 14);
    addNotes(slide, ["Current project modules and final workflow requested by the user."]);
  }

  const sourceNotes = [
    "All content is based on the local Java project in C:\\Programming\\SEM2\\ManufacturingWorkFlowManagement\\src.",
    "No external images, statistics or third-party claims were used.",
    "Main source areas: app, menu, manager, model, database and DSA packages.",
  ].join("\n");
  await fs.writeFile(path.join(ROOT, ".ppt-build", "source-notes.txt"), sourceNotes, "utf8");

  for (const [index, slide] of presentation.slides.items.entries()) {
    const stem = `slide-${String(index + 1).padStart(2, "0")}`;
    const png = await presentation.export({ slide, format: "png", scale: 1 });
    await fs.writeFile(path.join(OUT_DIR, `${stem}.png`), new Uint8Array(await png.arrayBuffer()));
    const layout = await slide.export({ format: "layout" });
    await fs.writeFile(path.join(OUT_DIR, `${stem}.layout.json`), await layout.text(), "utf8");
  }

  const montage = await presentation.export({ format: "webp", montage: true, scale: 0.5 });
  await fs.writeFile(path.join(OUT_DIR, "deck-montage.webp"), new Uint8Array(await montage.arrayBuffer()));

  const pptx = await PresentationFile.exportPptx(presentation);
  await pptx.save(FINAL_PPTX);
  console.log(FINAL_PPTX);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
