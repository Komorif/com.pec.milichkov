<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::post('/login', function (Request $request) {
    $token = $request->input('user_token');
    $user = \App\Models\User::where('user_token', $token)->first();

    if ($user) {
        return response()->json($user);
    }

    return response()->json(['message' => 'Invalid token'], 401);
});

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
