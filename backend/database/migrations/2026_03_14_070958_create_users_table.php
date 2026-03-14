<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('user_token')->unique();
            $table->string('first_name');
            $table->string('last_name');
            $table->string('surname');
            $table->string('educational_organization');
            $table->string('date_of_issue');
            $table->string('organization_level');
            $table->string('direction');
            $table->string('course');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('users');
    }
};
