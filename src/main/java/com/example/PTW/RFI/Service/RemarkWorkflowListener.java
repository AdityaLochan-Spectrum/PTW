// package com.example.PTW.RFI.Service;

// import java.util.Optional;

// import org.springframework.stereotype.Component;
// import org.springframework.transaction.event.TransactionPhase;
// import org.springframework.transaction.event.TransactionalEventListener;

// import com.example.PTW.RFI.CustomeService.WFApprovalIsReject;
// import com.example.PTW.RFI.CustomeService.WFApprovalIsRework;
// import com.example.PTW.RFI.CustomeService.WFApprovalisReleaseAndDevitation;
// import com.example.PTW.RFI.CustomeService.WFApprovalisReleased;
// import com.example.PTW.RFI.Entity.RFIProcessing;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// class RemarkWorkflowListener {

//     private final WFApprovalisReleased wfReleased;
//     private final WFApprovalIsRework wfRework;
//     private final WFApprovalIsReject wfReject;
//     private final WFApprovalisReleaseAndDevitation wfRelDev;
//     private final ApprovalUpdater approvalUpdater;

//     private static final String QA_PROCESS   = "P000738";
//     private static final String CLIENT_PROCESS = "P000739";

//     @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//     public void handle(RemarkSavedEvent ev) {
//         RFIProcessing r = ev.remark();
//         Long rfiId = r.getId().getRfiId();
//         String decision = Optional.ofNullable(r.getApproved()).orElse("").toLowerCase();

//         switch (decision) {
//             case "released" -> {
//                 wfReleased.startWorkflow(rfiId);
//                 approvalUpdater.apply(QA_PROCESS,     "G", rfiId);
//             }
//             case "rework" -> {
//                 wfRework.startReworkWorkflow(rfiId);
//                 approvalUpdater.apply(QA_PROCESS,     "I", rfiId);
//             }
//             case "reject" -> {
//                 wfReject.startRejectWorkflow(rfiId);
//                 approvalUpdater.apply(QA_PROCESS,     "J", rfiId);
//             }
//             case "release and deviation" -> {
//                 wfRelDev.startReleaseAndDeviation(rfiId);
//                 approvalUpdater.apply(QA_PROCESS,     "H", rfiId);
//             }
//         }

//         /* Client‑approval mirror (example) — extend as needed */
//         switch (decision) {
//             case "released" -> approvalUpdater.apply(CLIENT_PROCESS, "I", rfiId);
//             case "rework"   -> approvalUpdater.apply(CLIENT_PROCESS, "M", rfiId);
//             case "reject"   -> approvalUpdater.apply(CLIENT_PROCESS, "O", rfiId);
//             case "release and deviation" -> approvalUpdater.apply(CLIENT_PROCESS, "L", rfiId);
//         }
//     }
// }
